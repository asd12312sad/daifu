package com.hjh.erp.controller;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hjh.common.core.domain.entity.SysUser;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.RandomUtils;
import com.hjh.common.utils.SecurityUtils;
import com.hjh.common.utils.poi.ExcelUtil;
import com.hjh.common.utils.sign.Md5Utils;
import com.hjh.erp.domain.*;
import com.hjh.erp.mapper.*;
import com.hjh.erp.service.MerchantUserService;
import com.hjh.erp.service.impl.GoogleAuthenticator;
import com.hjh.system.mapper.SysUserMapper;
import com.hjh.usdttransfer.service.TRXWallet;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hjh.common.annotation.Log;
import com.hjh.common.core.controller.BaseController;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.enums.BusinessType;
import com.hjh.erp.service.IMerchantService;
import org.tron.common.crypto.ECKey;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * 商户管理Controller
 *
 * @author xiaobing
 * @date 2022-06-02
 */
@RestController
@RequestMapping("/erp/merchant")
public class MerchantController extends BaseController {
    @Autowired
    private IMerchantService merchantService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MerchantUserService merchantUserService;
    @Autowired
    private TRXWallet trxWallet;


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static SecureRandom random = new SecureRandom();

    /**
     * 查询商户管理列表
     */
    @PreAuthorize("@ss.hasPermi('erp:merchant:list')")
    @GetMapping("/list")
    public TableDataInfo list(Merchant merchant) {
        startPage();
        List<Merchant> page = merchantService.selectMerchantList(merchant);
        for (Merchant merchant1 : page) {
            merchant1.setRechargePrivateKey("**");
            merchant1.setTrxPrivateKey("**");
            merchant1.setUsdtPayPrivateKey("**");
            merchant1.setSign("**");

            merchant1.setTrxBalance(trxWallet.balanceOfTron(merchant1.getTrxAddress()));
            merchant1.setUsdtBalance(trxWallet.usdtBalanceOf(merchant1.getUsdtPayAddress()));
            merchant1.setUsdtPayTrxBalance(trxWallet.balanceOfTron(merchant1.getUsdtPayAddress()));

        }
        return getDataTable(page);
    }

    //    @GetMapping("/trialQuota/{id}")
    public AjaxResult trialQuota(@PathVariable("id") Long id, Integer trialQuota, String adminGoogleSign) {

        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }
        Merchant byId = merchantService.getById(id);
        if (byId.getTrialQuota() == null) {
            byId.setTrialQuota(trialQuota);
        }
        byId.setTrialQuota(byId.getTrialQuota() + (trialQuota));
        boolean b = merchantService.updateById(byId);
        return toAjax(b);
    }


    //    @GetMapping("/recharge/{id}")
    public AjaxResult recharge(@PathVariable("id") Long id, BigDecimal amount, String adminGoogleSign) {

        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }
        Merchant byId = merchantService.getById(id);
        if (byId.getBalance() == null) {
            byId.setBalance(amount);
        }
        byId.setBalance(byId.getBalance().add(amount));
        boolean b = merchantService.updateById(byId);
        return toAjax(b);
    }

    @GetMapping("/return/{id}")
    public String returnURL(@PathVariable("id") Long id, String amount, String sign, String ownerAddress) {
        Merchant byId = merchantService.getById(id);

        String md5 = SecureUtil.md5(byId.getSign() + amount + ownerAddress);
        if (md5.equals(sign)) {
            System.out.println("收到回调了amount=" + amount + "   sign= " + sign + "  ownerAddress=" + ownerAddress);
        } else {
            System.out.println("验证失败amount=" + amount + "   sign= " + sign + "  ownerAddress=" + ownerAddress);
        }
        return "SUCCESS";
    }

    /**
     * 导出商户管理列表
     */
    @PreAuthorize("@ss.hasPermi('erp:merchant:export')")
    @Log(title = "商户管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Merchant merchant) {
        List<Merchant> list = merchantService.selectMerchantList(merchant);

        for (Merchant merchant1 : list) {
            merchant1.setRechargePrivateKey("**");
            merchant1.setTrxPrivateKey("**");
            merchant1.setUsdtPayPrivateKey("**");
            merchant1.setSign("**");

        }
        ExcelUtil<Merchant> util = new ExcelUtil<Merchant>(Merchant.class);
        return util.exportExcel(list, "商户管理数据");
    }

    /**
     * 获取商户管理详细信息
     */
//    @GetMapping(value = "test/{id}")
    public AjaxResult test(@PathVariable("id") Long id, String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }
        return AjaxResult.success(merchantService.test(id));
    }

    /**
     * 获取商户管理详细信息
     */
//    @GetMapping(value = "testPay/{id}")
    public AjaxResult testPay(@PathVariable("id") Long id, @RequestParam String address, @RequestParam BigDecimal amount, String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }

        return AjaxResult.success(merchantService.testPay(id, address, amount));
    }


    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "bbbbbbb")
    public AjaxResult bbbbbbb(@RequestParam String payAddress, @RequestParam String address, @RequestParam BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("2")) > 0) {
            return AjaxResult.error("测试不可大于2USDT");
        }

        return AjaxResult.success(merchantService.bbbbbbb(payAddress, address, amount));
    }

    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "aaaaaaa")
    public AjaxResult aaaaaaa(@RequestParam String address) {

        return AjaxResult.success(merchantService.aaaaaaa(address));
    }

    /**
     * 获取商户管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:merchant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        Merchant merchant1 = merchantService.getById(id);
        merchant1.setRechargePrivateKey("**");
        merchant1.setTrxPrivateKey("**");
        merchant1.setUsdtPayPrivateKey("**");
        merchant1.setSign("**");

        return AjaxResult.success(merchant1);
    }

//获取更换安全邮箱code验证码

    @GetMapping(value = "/updateEmailCode")
    public AjaxResult updateEmailCode(@RequestParam String id, @RequestParam String email) {

        String code = RandomUtils.generateNumberString(6);// 验证码
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("亲爱的用户,您正在进行绑定安全邮箱操作，请将验证码发送给指定的平台工作人员，您的当前验证码是：" + code);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //multipart模式
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);//收件人邮箱user.getMail()
            mimeMessageHelper.setFrom("barizkein@gmail.com");//发件人邮箱
            mimeMessage.setSubject("绑定邮箱验证码");
            redisTemplate.opsForValue().set(id + ":" + "login:updateEmailCode:" + email, code, 5, TimeUnit.MINUTES);
            //启用html
            mimeMessageHelper.setText(stringBuilder.toString(), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return AjaxResult.error("发送验证码失败");
        }
        return AjaxResult.success();
    }


    @GetMapping(value = "/updateEmail")
    public AjaxResult updateEmail(@RequestParam String id, @RequestParam String email, @RequestParam String code,@RequestParam String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }

        String ivhCode =    redisTemplate.opsForValue().get(id + ":" + "login:updateEmailCode:" + email);
        if (code.equals(ivhCode)){
            Merchant merchant = merchantService.getById(id);
            merchant.setEmail(email);
            merchantService.updateById(merchant);
            return AjaxResult.success();
        }
        return AjaxResult.error("邮箱验证码错误");

    }



    /**
     * 获取商户管理详细信息
     */
//    @GetMapping(value = "updateGoogleSignCode")
    public AjaxResult updateGoogleSignCode(@RequestParam String id, @RequestParam String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }
        UpdateWrapper<MerchantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("google_sign_code", GoogleAuthenticator.genSecret());
        updateWrapper.set("last_login_date", null);
        updateWrapper.eq("merchant_id", id);
        return toAjax(merchantUserService.update(updateWrapper));
    }

    /**
     * 修改商户管理
     */
//    @PreAuthorize("@ss.hasPermi('erp:merchant:edit')")
//    @Log(title = "商户管理" , businessType = BusinessType.UPDATE)
//    @PutMapping
    public AjaxResult edit(@RequestBody Merchant merchant, @RequestParam String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }
        Merchant byId = merchantService.getById(merchant.getId());
        merchant.setPassword(byId.getPassword());
        merchant.setUpdateBy(SecurityUtils.getUsername());

        merchant.setUpdateTime(new Date());

        if (merchant.getTelegramBotToken() != null) {
            if (!"".equals(merchant.getTelegramBotToken())) {
                try {
                    String telegramJson = HttpUtil.get("https://api.telegram.org/bot" + merchant.getTelegramBotToken() + "/getUpdates");
                    JSONObject jsonObject = JSONObject.parseObject(telegramJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (jsonArray.getJSONObject(i).getJSONObject("message") != null) {
                            if (jsonArray.getJSONObject(i).getJSONObject("message").getJSONObject("chat") != null) {
                                if (jsonArray.getJSONObject(i).getJSONObject("message").getJSONObject("chat").getLong("id") != null) {
                                    Long aLong = jsonArray.getJSONObject(i).getJSONObject("message").getJSONObject("chat").getLong("id");
                                    if (aLong < 0) {
                                        merchant.setTelegramId(aLong.toString());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return AjaxResult.toAjax(merchantService.updateById(merchant));
    }

    /**
     * 删除商户管理
     */
//    @PreAuthorize("@ss.hasPermi('erp:merchant:remove')")
//    @Log(title = "商户管理" , businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids, String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign, sysUser.getGoogleSignCode())) {
            return AjaxResult.error("谷歌验证码错误");
        }
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(merchantService.removeByIds(idList));
    }
}

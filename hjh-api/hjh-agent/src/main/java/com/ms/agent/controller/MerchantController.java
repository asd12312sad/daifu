package com.ms.agent.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.agent.domain.*;
import com.ms.agent.mapper.*;
import com.ms.agent.service.AgentService;
import com.ms.agent.service.MerchantService;
import com.ms.agent.service.MerchantUserService;
import com.ms.agent.service.impl.TRXWallet;
import com.ms.agent.util.GoogleAuthenticator;
import com.ms.agent.util.Md5Utils;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "merchant")
public class MerchantController {


    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantUserMapper userMapper;

    @Autowired
    private MerchantMenuMapper menuMapper;

    @Autowired
    private MerchantUserService merchantUserService;

    @Autowired
    private MerchantRoleMapper roleMapper;

    @Autowired
    private AgentService agentService;


    @Autowired
    private MerchantRoleMenuMapper roleMenuMapper;

    @Autowired
    private MerchantUserRoleMapper userRoleMapper;
    private static SecureRandom random = new SecureRandom();

    @RequestMapping(value = "list", method = RequestMethod.GET)
    private R list(Merchant merchant, Integer pageSize, Integer pageNumber) {


        QueryWrapper<Merchant> merchantQueryWrapper = new QueryWrapper<>();

        if (merchant.getName() != null) {
            merchantQueryWrapper = merchantQueryWrapper.like("name", merchant.getName());
        }
        if (merchant.getAccount() != null) {
            merchantQueryWrapper = merchantQueryWrapper.like("account", merchant.getName());
        }
        merchantQueryWrapper = merchantQueryWrapper.eq("agent_id", CommonRequestHolder.getCurrentUserId());


        IPage<Merchant> page = new Page<>(pageNumber, pageSize);

        return R.success(merchantService.page(page, merchantQueryWrapper));
    }




    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    private R listAll() {
        return R.success(merchantService.list(new QueryWrapper<Merchant>().eq("agent_id", CommonRequestHolder.getCurrentUserId())));
    }

    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.success(merchantService.getById(id));
    }

    /**
     * 新增商户管理
     */
    @PostMapping("/add")
    public R add(@RequestBody Merchant merchant) {
        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());

        Boolean authcode = GoogleAuthenticator.authcode(merchant.getGoogleSign(), agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }

        if (merchant.getFee().compareTo(agent.getFee())<0) {
            return R.failed(401, "代收手续费不能小于代理商最低手续费");
        }
        if (merchant.getPayFee().compareTo(agent.getPayFee())<0) {
            return R.failed(401, "代付手续费不能小于代理商最低手续费");
        }

        Merchant account = merchantService.getOne(new QueryWrapper<Merchant>().eq("account", merchant.getAccount()));
        if (account != null) {
            return R.failed(401, "账号已存在");
        }
        merchant.setStatus(1);
        merchant.setAgentId(CommonRequestHolder.getCurrentUserId().intValue());
        merchant.setCreateTime(new Date());
        merchant.setSign(Md5Utils.hash(System.currentTimeMillis() + ""));
        JSONObject eCkey = TRXWallet.createAddress();

        String privateKey = eCkey.getString("apiKey");
        merchant.setTrxAddress(eCkey.getString("address"));
        merchant.setTrxPrivateKey(privateKey);
        JSONObject eCkey2 = TRXWallet.createAddress();


        merchant.setUsdtPayAddress(eCkey2.getString("address"));
        merchant.setUsdtPayPrivateKey(eCkey2.getString("apiKey"));

        JSONObject eCkey3 = TRXWallet.createAddress();

        merchant.setRechargeAddress(eCkey3.getString("address"));
        merchant.setRechargePrivateKey(eCkey3.getString("apiKey"));

        merchant.setBalance(new BigDecimal("100"));

        merchant.setGoogleSignCode(GoogleAuthenticator.genSecret());
        merchant.setPassword(SecurityUtils.encryptPassword(merchant.getPassword()));
        boolean save = merchantService.save(merchant);
        this.merchantService.createPayWallet(merchant);
        MerchantUser    merchantUser = new MerchantUser();
        merchantUser.setCreateTime(new Date());
        merchantUser.setMerchantId(merchant.getId());
        merchantUser.setUserName(merchant.getAccount());
        merchantUser.setGoogleSignCode(GoogleAuthenticator.genSecret());
        merchantUser.setNickName("管理员");
        merchantUser.setPassword(merchant.getPassword());
        userMapper.insert(merchantUser);

        MerchantRole merchantRole = new MerchantRole();
        merchantRole.setRoleName("管理员");
        roleMapper.insert(merchantRole);

        MerchantUserRole merchantUserRole = new MerchantUserRole();
        merchantUserRole.setUserId(merchantUser.getUserId());
        merchantUserRole.setRoleId(merchantRole.getRoleId());
        userRoleMapper.insert(merchantUserRole);
        List<MerchantMenu> merchantMenus = menuMapper.selectList(new QueryWrapper<>());
        for (MerchantMenu merchantMenu : merchantMenus) {
            MerchantRoleMenu merchantRoleMenu = new MerchantRoleMenu();
            merchantRoleMenu.setRoleId(merchantRole.getRoleId());
            merchantRoleMenu.setMenuId(merchantMenu.getMenuId());
            roleMenuMapper.insert(merchantRoleMenu);
        }

        this.merchantService.createPayWallet(merchant);



        if (merchant.getTelegramBotToken() !=null) {
            try {
                String telegramJson = HttpUtil.get("https://api.telegram.org/bot" + merchant.getTelegramBotToken() + "/getUpdates");
                JSONObject jsonObject = JSONObject.parseObject(telegramJson);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jsonArray.getJSONObject(i).getJSONObject("channel_post") != null) {
                        String telegramText = jsonArray.getJSONObject(i).getJSONObject("channel_post").getString("text");
                        if (telegramText.equals(merchant.getAccount())) {
                            merchant.setTelegramId(jsonArray.getJSONObject(i).getJSONObject("channel_post").getJSONObject("chat").getString("id"));
                            break;
                        }

                    }
                }
                merchantService.updateById(merchant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return R.success(save);
    }


    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "updateGoogleSignCode")
    public R updateGoogleSignCode(@RequestParam String id, @RequestParam String googleSignCode) {

        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSignCode, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }

        UpdateWrapper<MerchantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("google_sign_code", GoogleAuthenticator.genSecret());
        updateWrapper.set("last_login_date", null);
        updateWrapper.eq("merchant_id", id);
        return R.success(merchantUserService.update(updateWrapper));
    }

    /**
     * 重置商户密码
     */
    @GetMapping(value = "updatePassword")
    public R updateGoogleSignCode(@RequestParam String id,@RequestParam String password, @RequestParam String googleSignCode) {

        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSignCode, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }
        Merchant merchant = merchantService.getById(id);

        MerchantUser merchantUser = userMapper.selectOne(new QueryWrapper<MerchantUser>().eq("merchant_id", id).eq("user_name", merchant.getAccount()));

        merchant.setPassword(SecurityUtils.encryptPassword(password));
        merchantUser.setPassword(merchant.getPassword());
        userMapper.updateById(merchantUser);
        return R.success(merchantService.updateById(merchant));
    }

    /**
     * 重置手续费
     */
    @GetMapping(value = "replacement/fee")
    public R replacementFee(@RequestParam String id, @RequestParam BigDecimal fee,@RequestParam String googleSignCode) {


        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());

        Boolean authcode = GoogleAuthenticator.authcode(googleSignCode, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }
       if (agent.getBalance().compareTo(fee) < 0) {
            return R.failed(401, "您的可分配手续费余额不足");
        }
        Merchant merchant = merchantService.getById(id);
        agent.setBalance(agent.getBalance().subtract(fee));
        merchant.setBalance(merchant.getBalance().add(fee));
        agentService.updateById(agent);
        return R.success(merchantService.updateById(merchant));
    }
    /**
     * 修改商户管理
     */
    @PreAuthorize("@ss.hasPermi('erp:merchant:edit')")
    @PostMapping("edit")
    public R edit(@RequestBody Merchant merchant) {

        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());

        Boolean authcode = GoogleAuthenticator.authcode(merchant.getGoogleSign(), agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }

        if (merchant.getFee().compareTo(agent.getFee())<0) {
            return R.failed(401, "代收手续费不能小于代理商最低手续费");
        }
        if (merchant.getPayFee().compareTo(agent.getPayFee())<0) {
            return R.failed(401, "代付手续费不能小于代理商最低手续费");
        }
        Merchant byId = merchantService.getById(merchant.getId());
        merchant.setPassword(byId.getPassword());
        merchant.setUsdtAddress(byId.getUsdtAddress());

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
        return R.success(merchantService.updateById(merchant));
    }

}

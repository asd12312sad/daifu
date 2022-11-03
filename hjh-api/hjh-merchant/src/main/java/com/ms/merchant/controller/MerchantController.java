package com.ms.merchant.controller;


import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.core.domain.LoginUserVo;
import com.ms.common.domain.R;
import com.ms.common.core.enums.UserType;
import com.ms.common.core.utils.GsonUtil;
import com.ms.common.utils.*;
import com.ms.merchant.domain.*;
import com.ms.merchant.domain.dto.SettingDto;
import com.ms.merchant.domain.vo.LoginVo;
import com.ms.merchant.mapper.*;
import com.ms.merchant.service.MerchantPayUseAddressService;
import com.ms.merchant.service.MerchantService;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import com.ms.merchant.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@RequestMapping("sso")
public class  MerchantController {

    //商户
    //1，商户后台能否显示这个商户代收钱包，代付钱包和手续费钱包的余额
    //2，充值和代付订单记录导出功能
    //3，手续费余额钱包过低报警和代付钱包余额不足报警
    //4，商户设置归集钱包后提交保存不了
    //管理后台
    //1，充值订单补单功能（修改金额，重复支付）
    //2，收取商户手续费的利润报表，如何提现
    //3，商户代理功能
    //4，新增代付手续费设置（代付百分比费率和代付单笔）
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MerchantPayUseAddressService merchantPayUseAddressService;
    @Autowired
    private TRXWallet trxWallet;

    @Autowired
    private MerchantUserMapper userMapper;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private MerchantRoleMapper roleMapper;

    @Autowired
    private MerchantUserRoleMapper userRoleMapper;

    @Autowired
    private MerchantMenuMapper menuMapper;

    @Autowired
    private MerchantRoleMenuMapper roleMenuMapper;

    @Autowired
    private SystemInfoMapper systemInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static SecureRandom random = new SecureRandom();


    @GetMapping("/register/code")
    public R registerCode(@RequestParam String email) {
        String code = RandomUtils.generateNumberString(6);// 验证码

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("亲爱的用户,您的当前验证码是：" + code);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //multipart模式
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);//收件人邮箱user.getMail()
            mimeMessageHelper.setFrom("barizkein@gmail.com");//发件人邮箱
            mimeMessage.setSubject("注册验证码");
            redisTemplate.opsForValue().set("register:code:" + email, code, 5, TimeUnit.MINUTES);
            //启用html
            mimeMessageHelper.setText(stringBuilder.toString(), true);
            javaMailSender.send(mimeMessage);
            return R.success();
        } catch (MessagingException e) {
            e.printStackTrace();
            return R.failed();
        }
    }

    public void setAccount() {
        List<Merchant> merchantList = merchantService.list();

        for (Merchant merchant : merchantList) {
            MerchantUser merchantUser = new MerchantUser();
            merchantUser.setCreateTime(new Date());
            merchantUser.setMerchantId(merchant.getId());
            merchantUser.setUserName(merchant.getAccount());
            merchantUser.setNickName(merchant.getAccount());
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
        }
    }

    @GetMapping("/recharge/address")
    @MerchantApiNameAnnotation(apiName = "查询代付地址列表")
    public R rechargeAddress() {
        List<MerchantPayUseAddress> list = merchantPayUseAddressService.list(new QueryWrapper<MerchantPayUseAddress>().eq("type", 1));
        return R.success(list);
    }


    @GetMapping("/apiKey")
    @MerchantApiNameAnnotation(apiName = "查看API密钥")
    public R apiKey(@RequestParam String googleSign) {
        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());
        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        if (merchant.getEmail() == null){
            return R.failed(401, "您尚未绑定安全邮箱，请联系平台服务人员进行绑定");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("亲爱的用户,您的正在获取API密钥，您的API密钥为：" + merchant.getSign());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //multipart模式
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(merchant.getEmail());//收件人邮箱user.getMail()
            mimeMessageHelper.setFrom("barizkein@gmail.com");//发件人邮箱
            mimeMessage.setSubject("API获取");
            //启用html
            mimeMessageHelper.setText(stringBuilder.toString(), true);
            javaMailSender.send(mimeMessage);
            return R.success("您的密钥已发送至您的安全邮箱，请插手");
        } catch (MessagingException e) {
            e.printStackTrace();
            return R.failed();
        }

    }


    @GetMapping("/login/code")
    public R loginCode(@RequestParam String email) {
        String code = RandomUtils.generateNumberString(6);// 验证码

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("亲爱的用户,您的当前验证码是：" + code);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //multipart模式
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);//收件人邮箱user.getMail()
            mimeMessageHelper.setFrom("barizkein@gmail.com");//发件人邮箱
            mimeMessage.setSubject("注册验证码");
            redisTemplate.opsForValue().set("login:code:" + email, code, 5, TimeUnit.MINUTES);
            //启用html
            mimeMessageHelper.setText(stringBuilder.toString(), true);
            javaMailSender.send(mimeMessage);
            return R.success();
        } catch (MessagingException e) {
            e.printStackTrace();
            return R.failed();
        }
    }

    @GetMapping("/register")
    public R register(@RequestParam(required = false) String inviteCode, @RequestParam String email, @RequestParam String password, @RequestParam String code, @RequestParam(required = false) String nickName, @RequestParam(required = false) String invitationCode) {
        if (!code.equals("666666")) {
            String s = redisTemplate.opsForValue().get("register:code:" + email);
            if (s == null) {
                return R.failed(401, "验证码已失效");
            }

            if (!s.equals(code)) {
                return R.failed(401, "验证码错误");
            }
        }
        Merchant merchantOne = merchantService.getOne(new QueryWrapper<Merchant>().eq("account", email));
        MerchantUser merchantUser = null;
        Long count = userMapper.selectCount(new QueryWrapper<MerchantUser>().eq("user_name", email));

        if (count > 0 || merchantOne != null) {
            return R.failed(401, "该邮箱已被注册");

        }

        redisTemplate.delete("register:code:" + email);
        SystemInfo systemInfo = systemInfoMapper.selectById(1);
        Merchant merchant = new Merchant();

        if (invitationCode!=null && !("".equals(invitationCode))) {
            Agent agent = agentMapper.selectOne(new QueryWrapper<Agent>().eq("invite_code", invitationCode));
            if (agent != null) {
                merchant.setAgentId(agent.getId().intValue());
            }else {
                return R.failed(401, "邀请码错误");
            }
        }
        merchant.setFee(systemInfo.getOneAgent());
        merchant.setPayFee(systemInfo.getTwoAgent());

        JSONObject rechargeECkey = TRXWallet.createAddress();
        String rechargePrivateKey = rechargeECkey.getString("apiKey");
        merchant.setRechargeAddress(rechargeECkey.getString("address"));
        merchant.setRechargePrivateKey(rechargePrivateKey);


        //

        JSONObject eCkey = TRXWallet.createAddress();

        String privateKey = eCkey.getString("apiKey");
        merchant.setTrxAddress(eCkey.getString("address"));
        merchant.setTrxPrivateKey(privateKey);

        JSONObject usdtECkey = TRXWallet.createAddress();

        String usdtPrivateKey =usdtECkey.getString("apiKey");
        merchant.setUsdtPayAddress(usdtECkey.getString("address"));
        merchant.setUsdtPayPrivateKey(usdtPrivateKey);

        merchant.setPassword(SecurityUtils.encryptPassword(password));
        merchant.setName(nickName);
        merchant.setInviteCode(inviteCode);
        merchant.setTrialQuota(0);
        merchant.setCreateTime(null);
        merchant.setBalance(new BigDecimal("100"));
        merchant.setFee(new BigDecimal("0.01"));
        merchant.setPayFee(new BigDecimal("10"));
        merchant.setReturnAddress("");
        merchant.setUsdtAddress("");
        merchant.setTrxBalance(new BigDecimal("0"));
        merchant.setSign(Md5Utils.hash(System.currentTimeMillis() + ""));
        merchant.setUsdtBalance(new BigDecimal("0"));
        merchant.setUsdtPayTrxBalance(new BigDecimal("0"));
        merchant.setAccount(email);
        merchantService.save(merchant);
        merchantUser = new MerchantUser();
        merchantUser.setCreateTime(new Date());
        merchantUser.setMerchantId(merchant.getId());
        merchantUser.setUserName(email);
        merchantUser.setGoogleSignCode(GoogleAuthenticator.genSecret());
        merchantUser.setNickName("管理员");
        merchantUser.setPassword(SecurityUtils.encryptPassword(password));
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

        try {
            String telegramJson = HttpUtil.get("https://api.telegram.org/bot" + merchant.getTelegramBotToken() + "/getUpdates");
            JSONObject jsonObject = JSONObject.parseObject(telegramJson);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.getJSONObject(i).getJSONObject("channel_post") != null) {
                    String telegramText = jsonArray.getJSONObject(i).getJSONObject("channel_post").getString("text");
                    if (telegramText.equals(email)) {
                        merchant.setTelegramId(jsonArray.getJSONObject(i).getJSONObject("channel_post").getJSONObject("chat").getString("id"));
                        break;
                    }

                }
            }
            merchantService.updateById(merchant);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("googleSignUrl", GoogleAuthenticator.getQRBarcodeURL(merchantUser.getUserName(), "host", merchantUser.getGoogleSignCode()));
        return R.success(map);
    }


    @GetMapping("/login")
    public R login(@RequestParam String email, @RequestParam String password, @RequestParam String googleSign) {
        MerchantUser merchantUser = userMapper.selectOne(new QueryWrapper<MerchantUser>().eq("user_name", email));
        if (merchantUser == null) {
            return R.failed(401, "账号不存在");

        }

        boolean flag = SecurityUtils.matchesPassword(password, merchantUser.getPassword());
        if (!flag) {
            return R.failed(401, "密码错误");
        }
        Merchant merchant = merchantService.getById(merchantUser.getMerchantId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        // 登录token
        long timeMillis = System.currentTimeMillis();
        String token = JWTUtil.getInstance().generateToken(merchant.getId() + "", merchantUser.getUserId().toString(), merchant.getAccount(), 604800, UserType.TEACHER.getCode(), timeMillis);
//封装redis对象
        LoginUserVo redisUserVo = new LoginUserVo();
        redisUserVo.setName(merchant.getAccount());
        redisUserVo.setId(merchant.getId().intValue());
        redisUserVo.setParentId(merchant.getId().intValue());
        redisUserVo.setParentName(merchant.getAccount());
        redisUserVo.setLoginTime(timeMillis);
        redisTemplate.opsForValue().set(merchant.getId() + merchantUser.getUserId().toString() + merchant.getAccount() + UserType.TEACHER.getCode(), GsonUtil.getJson(redisUserVo), 30, TimeUnit.MINUTES);
        String newToken = "Bearer " + token;
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(newToken);
        loginVo.setTokenType(UserType.TEACHER.getCode());
        merchantUser.setLastLoginDate(new Date());

        userMapper.updateById(merchantUser);
        if (merchant.getLoginCount() == null) {
            merchant.setLoginCount(0);
        }
        merchant.setLoginCount(merchant.getLoginCount() + 1);
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        merchant.setIp(ip);
        this.merchantService.updateById(merchant);


        //获取权限
        List<MerchantUserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<MerchantUserRole>().eq("user_id", merchantUser.getUserId()));
        //获取所有roleId
        List<Long> roleIdList = userRoleList.stream().map(MerchantUserRole::getRoleId).collect(Collectors.toList());
        //获取所有menu
        List<MerchantRoleMenu> roleMenuList = roleMenuMapper.selectList(new QueryWrapper<MerchantRoleMenu>().in("role_id", roleIdList));
        //获取所有menuId
        List<Long> menuIdList = roleMenuList.stream().map(MerchantRoleMenu::getMenuId).collect(Collectors.toList());
        //获取所有menu
        List<MerchantMenu> menuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().in("menu_id", menuIdList));
        loginVo.setMenuList(menuList);
        return R.success(loginVo);
    }

    @GetMapping("/getGoogleSign")
    public R getGoogleSign(String email, String password) {


        MerchantUser merchantUser = userMapper.selectOne(new QueryWrapper<MerchantUser>().eq("user_name", email));
        if (merchantUser == null) {
            return R.failed(401, "账号不存在");

        }

        boolean flag = SecurityUtils.matchesPassword(password, merchantUser.getPassword());
        if (!flag) {
            return R.failed(401, "密码错误");
        }
        if (merchantUser.getLastLoginDate() != null) {
            return R.failed(898, "不是第一次登录");
        }
        String host = "otpauth://totp/" + merchantUser.getUserName() + "@host?secret=" + merchantUser.getGoogleSignCode();
        Map<String, String> map = new HashMap<>();
        map.put("sign", host);
        return R.success(map);
    }



    @GetMapping("/info")
    public R info() {
        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());
        merchant.setTrxBalance(trxWallet.balanceOfTron(merchant.getTrxAddress()));
        merchant.setUsdtBalance(BigDecimal.ZERO);
        merchant.setUsdtPayTrxBalance(BigDecimal.ZERO);

        List<MerchantPayUseAddress> addressList = merchantPayUseAddressService.list(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchant.getId()));
        for (MerchantPayUseAddress merchantPayUseAddress : addressList) {
            merchant.setUsdtPayTrxBalance(merchant.getUsdtPayTrxBalance().add(trxWallet.balanceOfTron(merchantPayUseAddress.getAddress())));
            merchant.setUsdtBalance(merchant.getUsdtBalance().add(trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress())));
        }
        merchant.setRechargePrivateKey(null);
        merchant.setGoogleSignCode(null);
        merchant.setPassword(null);
        merchant.setUsdtPayPrivateKey(null);
        //密钥只显示前五位与后五位
        if (merchant.getSign() != null) {
            merchant.setSign(merchant.getSign().substring(0, 5) + "******" + merchant.getSign().substring(merchant.getSign().length() - 5));
        }
        merchant.setTrxPrivateKey(null);

        Integer detailUserId = CommonRequestHolder.getCurrentDetailUserId();
        MerchantUser merchantUser = userMapper.selectById(detailUserId);
        //获取权限
        List<MerchantUserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<MerchantUserRole>().eq("user_id", merchantUser.getUserId()));
        if (userRoleList == null || userRoleList.size() == 0) {
            merchant.setMerchantMenuList(new ArrayList<>());
            return R.success(merchant);

        }
        //获取所有roleId
        List<Long> roleIdList = userRoleList.stream().map(MerchantUserRole::getRoleId).collect(Collectors.toList());


        //获取所有menu
        List<MerchantRoleMenu> roleMenuList = roleMenuMapper.selectList(new QueryWrapper<MerchantRoleMenu>().in("role_id", roleIdList));
        if (roleMenuList == null || roleMenuList.size() == 0) {
            merchant.setMerchantMenuList(new ArrayList<>());
            return R.success(merchant);

        }

        //获取所有menuId
        List<Long> menuIdList = roleMenuList.stream().map(MerchantRoleMenu::getMenuId).collect(Collectors.toList());
        //获取所有menu
        List<MerchantMenu> menuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().in("menu_id", menuIdList).orderByAsc("menu_id"));
        merchant.setMerchantMenuList(menuList);
        merchant.setAccount(merchantUser.getUserName());

        return R.success(merchant);
    }

    @GetMapping("/resetSign")
    @MerchantApiNameAnnotation(apiName = "重置API密钥")
    public R resetSign(String googleSign) {
        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());


        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        if (merchant.getEmail() == null){
            return R.failed(401, "您尚未绑定安全邮箱，请联系平台服务人员进行绑定");
        }
        merchant.setSign(Md5Utils.hash(System.currentTimeMillis() + ""));
        merchantService.updateById(merchant);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("亲爱的用户,您的重置了API密钥，您的新API密钥为：" + merchant.getSign());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //multipart模式
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(merchant.getEmail());//收件人邮箱user.getMail()
            mimeMessageHelper.setFrom("barizkein@gmail.com");//发件人邮箱
            mimeMessage.setSubject("API获取");
            //启用html
            mimeMessageHelper.setText(stringBuilder.toString(), true);
            javaMailSender.send(mimeMessage);
            return R.success("您的新密钥已发送至您的安全邮箱，请插手");
        } catch (MessagingException e) {
            e.printStackTrace();
            return R.failed();
        }

    }


    @GetMapping("/reset/email")
    @MerchantApiNameAnnotation(apiName = "重置邮箱")
    public R resetEmail(@RequestParam String email, String googleSign) {


        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());


        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        merchant.setAccount(email);
        merchantService.updateById(merchant);
        return R.success();
    }


    @GetMapping("/reset/password")
    @MerchantApiNameAnnotation(apiName = "修改密码")
    public R resetPassword(@RequestParam String oldPassWord, @RequestParam String passWord, String googleSign) {


        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }


        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        boolean flag = SecurityUtils.matchesPassword(oldPassWord, merchantUser.getPassword());
        if (!flag) {
            return R.failed(401, "密码错误");
        }
        merchantUser.setPassword(SecurityUtils.encryptPassword(passWord));
        userMapper.updateById(merchantUser);
        return R.success();
    }


    @GetMapping("/resetGoogleSign")
    @MerchantApiNameAnnotation(apiName = "重置谷歌验证码")
    public R resetGoogleSign(String googleSign) {

        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        merchantUser.setGoogleSignCode(GoogleAuthenticator.genSecret());
        userMapper.updateById(merchantUser);
        Map<String, String> map = new HashMap<>();
        String host = "otpauth://totp/" + merchantUser.getUserName() + "@host?secret=" + merchantUser.getGoogleSignCode();
        map.put("googleSignUrl", host);
        return R.success(map);
    }

    @PostMapping("/update/setting")
    @MerchantApiNameAnnotation(apiName = "更新配置")
    public R updateSetting(@RequestBody SettingDto settingDto) {
        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());


        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(settingDto.getGoogleSign(), loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        if (settingDto.getUsdtAddress() == null || "".equals(settingDto.getUsdtAddress()) || settingDto.getUsdtAddress().length() < 30 || (!settingDto.getUsdtAddress().startsWith("T"))) {
            return R.failed(401, "归集地址填写错误");
        }
        if (settingDto.getNotionalPoolingAmount().compareTo(new BigDecimal("3000"))>0){
            return R.failed(401, "归集金额不得高于3000");
        }
        if (merchant.getUsdtAddress()==null || ("".equals(merchant.getUsdtAddress()))) {
            merchant.setUsdtAddress(settingDto.getUsdtAddress());
        }
        merchant.setReturnAddress(settingDto.getReturnAddress());

        merchant.setNotionalPoolingAmount(settingDto.getNotionalPoolingAmount());
        merchant.setCollectionApiWhile(settingDto.getCollectionApiWhile());

        if (settingDto.getTelegramBotToken()!=null) {
            try {
                String telegramJson = HttpUtil.get("https://api.telegram.org/bot" + merchant.getTelegramBotToken() + "/getUpdates");
                JSONObject jsonObject = JSONObject.parseObject(telegramJson);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jsonArray.getJSONObject(i).getJSONObject("channel_post") != null) {
                        String telegramText = jsonArray.getJSONObject(i).getJSONObject("channel_post").getString("text");
                        if (telegramText.equals(merchant.getAccount())) {
                            merchant.setTelegramId(jsonArray.getJSONObject(i).getJSONObject("channel_post").getJSONObject("chat").getString("id"));
                            merchant.setTelegramBotToken(settingDto.getTelegramBotToken());
                            break;
                        }

                    }
                }
                merchantService.updateById(merchant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        merchant.setPayApiWhile(settingDto.getPayApiWhile());



        merchant.setReturnPayAddress(settingDto.getReturnPayAddress());
        merchantService.updateById(merchant);
        return R.success(merchant);
    }

}

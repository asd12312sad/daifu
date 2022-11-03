package com.ms.merchant.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.Merchant;
import com.ms.merchant.domain.MerchantPayUseAddress;
import com.ms.merchant.domain.MerchantUser;
import com.ms.merchant.domain.vo.HomeCountVO;
import com.ms.merchant.mapper.MerchantPayUseAddressMapper;
import com.ms.merchant.mapper.MerchantUserMapper;
import com.ms.merchant.service.IMerchantPayOrderService;
import com.ms.merchant.service.MerchantService;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("home")
public class HomeController {
    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;

    @Autowired
    private MerchantUserMapper userMapper;


    @GetMapping("/sum")
    public R index() {

        HomeCountVO homeCountVO = merchantPayOrderService.selectHomeCount();
        return R.success(homeCountVO);
    }

    @GetMapping("/collection/header")
    public R collectionHeader() {
        return R.success(merchantPayOrderService.collectionHeader());
    }

    @GetMapping("/pay/header")
    public R payHeader() {
        return R.success(merchantPayOrderService.payHeader());
    }

    @GetMapping("/week/sum")
    public R weekSum() {
        Map<String, Object> map = merchantPayOrderService.weekSum();
        return R.success(map);
    }

    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "test")
    @MerchantApiNameAnnotation(apiName = "测试代收")
    public R test(String googleSign) {
        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        return R.success(merchantService.test());
    }

    /**
     * 获取商户管理详细信息
     */
    @MerchantApiNameAnnotation(apiName = "测试代付")
//    @GetMapping(value = "testPay")
    public R testPay(@RequestParam String address, @RequestParam BigDecimal amount, String googleSign) {
        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
       if (amount.compareTo(BigDecimal.ZERO)<=0) {
           return R.failed(401, "金额不能小于0");
       }

       if(amount.compareTo(BigDecimal.valueOf(10))>0) {
           return R.failed(401, "金额不能大于10U");
       }
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        return R.success(merchantService.testPay(address, amount));
    }
}

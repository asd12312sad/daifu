package com.ms.merchant.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.MerchantPayUseAddress;
import com.ms.merchant.domain.MerchantUser;
import com.ms.merchant.mapper.MerchantMapper;
import com.ms.merchant.mapper.MerchantPayUseAddressMapper;
import com.ms.merchant.mapper.MerchantUserMapper;
import com.ms.merchant.service.MerchantPayUseAddressService;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MerchantPayUseAddressServiceImpl extends ServiceImpl<MerchantPayUseAddressMapper, MerchantPayUseAddress> implements MerchantPayUseAddressService {

    @Autowired
    private MerchantUserMapper userMapper;


    @Override
    public R create(String googleSign) {

        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
       MerchantPayUseAddress merchantPayUseAddress = new MerchantPayUseAddress();
        merchantPayUseAddress.setMerchantId(CommonRequestHolder.getCurrentUserId());

        JSONObject eCkey = TRXWallet.createAddress();

        String privateKey = eCkey.getString("apiKey");
        merchantPayUseAddress.setAddress(eCkey.getString("address"));
        merchantPayUseAddress.setPrivateKey(privateKey);
        merchantPayUseAddress.setHexAddress(privateKey);
        merchantPayUseAddress.setPrivateKeyBase(privateKey);
        merchantPayUseAddress.setCreateTime(new Date());
        merchantPayUseAddress.setUpdateTime(new Date());
        return save(merchantPayUseAddress)?R.success():R.failed();
    }
}

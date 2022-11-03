package com.ms.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.MerchantPayUseAddress;
import com.ms.merchant.service.MerchantPayUseAddressService;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequestMapping(value = "/pay/use/address")
public class MerchantPayUseAddressController {

    @Autowired
    private MerchantPayUseAddressService merchantPayUseAddressService;

    @Autowired
    private TRXWallet trxWallet;

    @MerchantApiNameAnnotation(apiName = "创建代付地址")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public R create(String googleSign) {
        return merchantPayUseAddressService.create(googleSign);
    }




    @MerchantApiNameAnnotation(apiName = "获取代付地址列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R list() {

        List<MerchantPayUseAddress> addressList = merchantPayUseAddressService.list(
                new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", CommonRequestHolder.getCurrentUserId()));

        for (MerchantPayUseAddress merchantPayUseAddress : addressList) {
            merchantPayUseAddress.setTrxBalance(trxWallet.balanceOfTron(merchantPayUseAddress.getAddress()));
            if (merchantPayUseAddress.getTrxBalance() == null) {
              merchantPayUseAddress.setRemark("未激活");
            }

            merchantPayUseAddress.setUsdtBalance(trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress()));
            merchantPayUseAddress.setPrivateKey(null);
            merchantPayUseAddress.setPrivateKeyBase(null);
            merchantPayUseAddress.setHexAddress(null);
        }
        return    R.success( addressList);
    }
}

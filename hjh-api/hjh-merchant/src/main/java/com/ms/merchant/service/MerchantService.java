package com.ms.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.merchant.domain.Merchant;

import java.math.BigDecimal;

public interface MerchantService  extends IService<Merchant>  {
    Object test();

    Object testPay(String address, BigDecimal amount);

    void createPayWallet(Merchant merchant);
}

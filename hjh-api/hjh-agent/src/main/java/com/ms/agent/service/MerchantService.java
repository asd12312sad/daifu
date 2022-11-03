package com.ms.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.agent.domain.Merchant;

import java.math.BigDecimal;

public interface MerchantService extends IService<Merchant> {

    void createPayWallet(Merchant merchant);

    Object test(Long merchantId);

    Object testPay(String address, BigDecimal amount, Long merchantId);
}

package com.ms.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.common.domain.R;
import com.ms.merchant.domain.MerchantPayUseAddress;

public interface MerchantPayUseAddressService extends IService<MerchantPayUseAddress> {
    R create(String googleSign);


}

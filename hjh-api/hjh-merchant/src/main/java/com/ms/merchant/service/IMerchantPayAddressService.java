package com.ms.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.merchant.domain.MerchantPayAddress;

import java.util.List;

public interface IMerchantPayAddressService extends IService<MerchantPayAddress> {
    IPage<MerchantPayAddress> selectMerchantPayAddressList(IPage<MerchantPayAddress> page, MerchantPayAddress merchantPayAddress);

    List<MerchantPayAddress> selectMerchantPayAddressExport(MerchantPayAddress merchantPayAddress);

}

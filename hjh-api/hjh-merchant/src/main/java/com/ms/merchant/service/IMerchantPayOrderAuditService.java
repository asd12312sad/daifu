package com.ms.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.MerchantPayOrderAudit;

public interface IMerchantPayOrderAuditService extends IService<MerchantPayOrderAudit> {
    IPage<MerchantPayOrder> selectPage(IPage<MerchantPayOrder> page, MerchantPayOrder merchantPayOrder);

    IPage<MerchantPayAddress> selectCollectionPage(IPage<MerchantPayAddress> page, MerchantPayAddress merchantPayAddress);

}

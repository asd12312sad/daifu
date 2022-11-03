package com.ms.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.MerchantPayOrderAudit;
import org.apache.ibatis.annotations.Param;

public interface MerchantPayOrderAuditMapper extends BaseMapper<MerchantPayOrderAudit> {
    IPage<MerchantPayOrder> selectPageByBean(IPage<MerchantPayOrder> page, @Param("merchantPayOrder") MerchantPayOrder merchantPayOrder);

    IPage<MerchantPayAddress> selectCollectionPage(IPage<MerchantPayAddress> page, @Param("merchantPayAddress") MerchantPayAddress merchantPayAddress);
}

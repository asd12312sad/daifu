package com.ms.merchant.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.MerchantPayOrderAudit;
import com.ms.merchant.mapper.MerchantPayOrderAuditMapper;
import com.ms.merchant.service.IMerchantPayOrderAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantPayOrderAuditServiceImpl extends ServiceImpl<MerchantPayOrderAuditMapper, MerchantPayOrderAudit> implements IMerchantPayOrderAuditService  {

    @Autowired
    private MerchantPayOrderAuditMapper merchantPayOrderAuditMapper;
    @Override
    public IPage<MerchantPayOrder> selectPage(IPage<MerchantPayOrder> page, MerchantPayOrder merchantPayOrder) {
        merchantPayOrder.setMerchantId(CommonRequestHolder.getCurrentUserId().toString());
        if ("0".equals(merchantPayOrder.getMessage())){
            merchantPayOrder.setPrivateKey("0");
            merchantPayOrder.setMessage(null);
        }
        return    this.merchantPayOrderAuditMapper.selectPageByBean(page,merchantPayOrder);
    }

    @Override
    public IPage<MerchantPayAddress> selectCollectionPage(IPage<MerchantPayAddress> page, MerchantPayAddress merchantPayAddress) {
        merchantPayAddress.setMerchantId(CommonRequestHolder.getCurrentUserId().toString());
        if ("0".equals(merchantPayAddress.getMessage())){
            merchantPayAddress.setPrivateKey("0");
            merchantPayAddress.setMessage(null);
        }
        return    this.merchantPayOrderAuditMapper.selectCollectionPage(page,merchantPayAddress);
    }
}

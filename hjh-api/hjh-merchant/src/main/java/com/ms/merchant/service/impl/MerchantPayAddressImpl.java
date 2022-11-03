package com.ms.merchant.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.mapper.MerchantPayAddressMapper;
import com.ms.merchant.service.IMerchantPayAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantPayAddressImpl extends ServiceImpl<MerchantPayAddressMapper,MerchantPayAddress> implements IMerchantPayAddressService {
    @Override
    public IPage<MerchantPayAddress> selectMerchantPayAddressList(IPage<MerchantPayAddress> page, MerchantPayAddress merchantPayAddress) {
        if ("0".equals(merchantPayAddress.getMessage())){
            merchantPayAddress.setPrivateKey("0");
            merchantPayAddress.setMessage(null);
        }

        return baseMapper.selectMerchantPayAddressList(page,merchantPayAddress);
    }

    @Override
    public List<MerchantPayAddress> selectMerchantPayAddressExport(MerchantPayAddress merchantPayAddress) {
        merchantPayAddress.setMerchantId(CommonRequestHolder.getCurrentUserId().toString());

        return baseMapper.selectMerchantPayAddressExport(merchantPayAddress);

    }
}

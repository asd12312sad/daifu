package com.ms.agent.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.agent.domain.MerchantPayAddress;
import com.ms.agent.mapper.MerchantPayAddressMapper;
import com.ms.agent.service.IMerchantPayAddressService;
import com.ms.common.utils.CommonRequestHolder;
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
        merchantPayAddress.setAgentId(CommonRequestHolder.getCurrentUserId().intValue());

        return baseMapper.selectMerchantPayAddressExport(merchantPayAddress);

    }
}

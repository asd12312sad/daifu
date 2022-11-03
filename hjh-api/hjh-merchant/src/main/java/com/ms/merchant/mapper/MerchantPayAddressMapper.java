package com.ms.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ms.merchant.domain.MerchantPayAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantPayAddressMapper extends BaseMapper<MerchantPayAddress> {
    IPage<MerchantPayAddress> selectMerchantPayAddressList(IPage<MerchantPayAddress> page, @Param("merchantPayAddress") MerchantPayAddress merchantPayAddress);

    List<MerchantPayAddress> selectMerchantPayAddressExport(@Param("merchantPayAddress")MerchantPayAddress merchantPayAddress);

}

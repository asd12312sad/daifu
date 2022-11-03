package com.hjh.erp.mapper;

import com.hjh.erp.domain.MerchantPayAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户付款地址Mapper接口
 *
 * @author ruoyi
 * @date 2022-06-03
 */
public interface MerchantPayAddressMapper extends BaseMapper<MerchantPayAddress>
{

/**
 * 查询商户付款地址列表
 *
 * @param merchantPayAddress 商户付款地址
 * @return 商户付款地址集合
 */
public List<MerchantPayAddress> selectMerchantPayAddressList(@Param("merchantPayAddress")MerchantPayAddress merchantPayAddress, @Param("params") Map<String, Object> params);


    /**
     * 查询商户付款地址
     *
     * @param address 商户付款地址主键
     * @return 商户付款地址
     */
    public MerchantPayAddress selectMerchantPayAddressByAddress(String address);

}

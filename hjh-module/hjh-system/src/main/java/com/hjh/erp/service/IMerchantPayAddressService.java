package com.hjh.erp.service;

import com.hjh.erp.domain.MerchantPayAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 商户付款地址Service接口
 *
 * @author ruoyi
 * @date 2022-06-03
 */
public interface IMerchantPayAddressService  extends IService<MerchantPayAddress>
{

    /**
     * 查询商户付款地址列表
     *
     * @param merchantPayAddress 商户付款地址
     * @return 商户付款地址集合
     */
    public List<MerchantPayAddress> selectMerchantPayAddressList(MerchantPayAddress merchantPayAddress);

}

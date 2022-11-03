package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import com.hjh.erp.domain.MerchantPayAddress;
import com.hjh.erp.mapper.MerchantPayAddressMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.service.IMerchantPayAddressService;

/**
 * 商户付款地址Service业务层处理
 *
 * @author ruoyi
 * @date 2022-06-03
 */
@Service
public class MerchantPayAddressServiceImpl extends ServiceImpl<MerchantPayAddressMapper, MerchantPayAddress> implements IMerchantPayAddressService
{



    /**
     * 查询商户付款地址列表
     *
     * @param merchantPayAddress 商户付款地址
     * @return 商户付款地址集合
     */
    @Override
    public List<MerchantPayAddress> selectMerchantPayAddressList(MerchantPayAddress merchantPayAddress){
        if (merchantPayAddress.getParams() == null){
            merchantPayAddress.setParams(new HashMap<>());
        }
       return  this.baseMapper.selectMerchantPayAddressList(merchantPayAddress,merchantPayAddress.getParams());

    }
}

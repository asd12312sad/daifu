package com.ms.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.vo.HomeCountVO;
import com.ms.merchant.domain.vo.OrderHeaderVo;

import java.util.List;
import java.util.Map;

public interface IMerchantPayOrderService  extends IService<MerchantPayOrder> {

    IPage<MerchantPayOrder> selectMerchantPayOrderList(IPage<MerchantPayOrder> page, MerchantPayOrder merchantPayOrder);

    HomeCountVO selectHomeCount();

    Map<String, Object> weekSum();

    List<MerchantPayAddress> selectMerchantPayOrderExport(MerchantPayOrder merchantPayOrder);

    Map collectionHeader();

    Map payHeader();

}

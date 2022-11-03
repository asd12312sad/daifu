package com.ms.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.agent.domain.MerchantPayAddress;
import com.ms.agent.domain.MerchantPayOrder;
import com.ms.agent.pojo.vo.FinanceListVo;
import com.ms.agent.pojo.vo.HomeCountVO;
import com.ms.agent.pojo.vo.SysSettingVO;

import java.util.List;
import java.util.Map;

public interface IMerchantPayOrderService  extends IService<MerchantPayOrder> {

    IPage<MerchantPayOrder> selectMerchantPayOrderList(IPage<MerchantPayOrder> page, MerchantPayOrder merchantPayOrder);

    HomeCountVO selectHomeCount();

    Map<String, Object> weekSum();

    List<MerchantPayOrder> selectMerchantPayOrderExport(MerchantPayOrder merchantPayOrder);

    Map collectionHeader();

    Map payHeader();

    IPage<FinanceListVo> financeList(FinanceListVo financeListVo);

     List<FinanceListVo> financeExpoit(FinanceListVo financeListVo) ;

    SysSettingVO financeInfo();

}

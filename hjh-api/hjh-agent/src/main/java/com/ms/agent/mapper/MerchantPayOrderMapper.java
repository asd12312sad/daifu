package com.ms.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ms.agent.domain.MerchantPayAddress;
import com.ms.agent.domain.MerchantPayOrder;
import com.ms.agent.pojo.vo.FinanceListVo;
import com.ms.agent.pojo.vo.HomeCountVO;
import com.ms.agent.pojo.vo.OrderHeaderVo;
import com.ms.agent.pojo.vo.SysSettingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantPayOrderMapper  extends BaseMapper<MerchantPayOrder> {
    IPage<MerchantPayOrder> selectOrder(IPage<MerchantPayOrder> page, @Param("merchantPayOrder") MerchantPayOrder merchantPayOrder);

    HomeCountVO selectHomeCollectCount(@Param("agentId") Long agentId);

    HomeCountVO selectHomePayCount(@Param("agentId") Long agentId);

    HomeCountVO selectHomeCountPay(@Param("date") String date, @Param("agentId") Long agentId);

    HomeCountVO selectHomeCountCollect(@Param("date") String date, @Param("agentId") Long currentUserId);

    List<MerchantPayOrder> selectOrderExport(@Param("merchantPayOrder") MerchantPayOrder merchantPayOrder);

    OrderHeaderVo collectionTodayHeader(@Param("agentId") Long agentId);

    OrderHeaderVo collectionHeader(@Param("agentId") Long agentId);

    OrderHeaderVo passageHeader(@Param("agentId") Long agentId);

    OrderHeaderVo successHeader(@Param("agentId") Long agentId);

    OrderHeaderVo failHeader(@Param("agentId") Long agentId);

    IPage<FinanceListVo> financeList(IPage<FinanceListVo> page, @Param("financeListVo") FinanceListVo financeListVo);

    List<FinanceListVo> financeExpoit(@Param("financeListVo") FinanceListVo financeListVo);


    SysSettingVO financeInfo(@Param("agentId") Long agentId);
}

package com.ms.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.vo.HomeCountVO;
import com.ms.merchant.domain.vo.OrderHeaderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantPayOrderMapper  extends BaseMapper<MerchantPayOrder> {
    IPage<MerchantPayOrder> selectOrder(IPage<MerchantPayOrder> page,@Param("merchantPayOrder") MerchantPayOrder merchantPayOrder);

    HomeCountVO selectHomeCollectCount(@Param("userId") Long userId);

    HomeCountVO selectHomePayCount(@Param("userId") Long userId);

    HomeCountVO selectHomeCountPay(@Param("date")String date,@Param("userId") Long userId);

    HomeCountVO selectHomeCountCollect(@Param("date")String date,@Param("userId") Long userId);

    List<MerchantPayAddress> selectOrderExport(@Param("merchantPayOrder")MerchantPayOrder merchantPayOrder);

    OrderHeaderVo collectionTodayHeader(Long id);


    OrderHeaderVo collectionHeader(Long id);


    OrderHeaderVo payTodayHeader(Long id);

    OrderHeaderVo payHeader(Long id);

    OrderHeaderVo passageHeader(Long id);

    OrderHeaderVo successHeader(Long id);

    OrderHeaderVo failHeader(Long id);
}

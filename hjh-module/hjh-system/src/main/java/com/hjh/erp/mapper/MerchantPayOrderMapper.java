package com.hjh.erp.mapper;

import com.hjh.erp.domain.HomeCountVO;
import com.hjh.erp.domain.MerchantPayOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户代付记录Mapper接口
 *
 * @author xiaobing
 * @date 2022-06-10
 */
public interface MerchantPayOrderMapper extends BaseMapper<MerchantPayOrder>
{

    /**
     * 查询商户代付记录列表
     *
     * @param merchantPayOrder 商户代付记录
     * @return 商户代付记录集合
     */
    public List<MerchantPayOrder> selectMerchantPayOrderList(@Param("merchantPayOrder")MerchantPayOrder merchantPayOrder, @Param("params") Map<String, Object> params);


    HomeCountVO selectHomePayCount();


    HomeCountVO selectHomeCollectCount();


    HomeCountVO selectHomeCountPay(@Param("date") String date);

    HomeCountVO selectHomeCountCollect(@Param("date")String date);
}

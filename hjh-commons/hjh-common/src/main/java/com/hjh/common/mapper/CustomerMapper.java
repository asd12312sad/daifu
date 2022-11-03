package com.hjh.common.mapper;

import com.hjh.common.core.domain.entity.SysDictData;
import com.hjh.common.domain.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjh.common.pojo.dto.DistributionStatisticsDto;
import com.hjh.common.pojo.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户Mapper接口
 *
 * @author xiaobing
 * @date 2021-08-23
 */
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 查询客户列表
     *
     * @param customer 客户
     * @return 客户集合
     */
    public List<CustomerListVo> selectCustomerListDtoList(@Param("customer") CustomerListVo customer, @Param("params") Map<String, Object> params, @Param("userId") Long userId);


    /**
     * 查询跟访记录列列表
     *
     * @param customerVisitPage 跟访记录列
     * @return 跟访记录列集合
     */
    public List<CustomerVisitPage> selectCustomerVisitPageList(@Param("customerVisitPage") CustomerVisitPage customerVisitPage, @Param("params") Map<String, Object> params);

    /**
     * 客户分配统计
     *
     * @param dictValueList 分配统计客户来源
     * @return 客户分配统计
     */
    List<HashMap<String, Object>> distributionStatisticsList(@Param("list") List<SysDictData> dictValueList, @Param("dto") DistributionStatisticsDto statisticsDto);

    List<StatisticsPageVo> selectStatistics(@Param("dto") DistributionStatisticsDto statisticsDto);

    List<VisitStatisticsPageVo> visitStatisticsList(@Param("dto") DistributionStatisticsDto statisticsDto);

    List<RingStatisticsPageVo> ringList(@Param("dto") DistributionStatisticsDto statisticsDto);

    Integer selectAllToShopCount(@Param("dto") DistributionStatisticsDto statisticsDto);

    UserStatisticsHeaderVo selectUserStatisticsInfo(@Param("dto") DistributionStatisticsDto statisticsDto);

    List<UserStatisticsPageVo> selectUserStatisticsList(@Param("dto") DistributionStatisticsDto statisticsDto);

    List<CustomerListVo> selectVxToDoList(@Param("userId") Long userId);
}

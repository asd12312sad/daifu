package com.hjh.common.mapper;

import com.hjh.common.domain.CmsCustomerDailyAssign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户每日汇总Mapper接口
 *
 * @author xiaobing
 * @date 2021-08-24
 */
public interface CmsCustomerDailyAssignMapper extends BaseMapper<CmsCustomerDailyAssign> {

    /**
     * 查询客户每日汇总列表
     *
     * @param cmsCustomerDailyAssign 客户每日汇总
     * @return 客户每日汇总集合
     */
    public List<CmsCustomerDailyAssign> selectCmsCustomerDailyAssignList(@Param("cmsCustomerDailyAssign") CmsCustomerDailyAssign cmsCustomerDailyAssign, @Param("params") Map<String, Object> params);


    /**
     * 获取客户每日详情 装修风格 客户意向
     *
     * @param id 客户每日汇总ID
     * @return 获取客户每日详情
     */
    public CmsCustomerDailyAssign selectCmsCustomerDailyAssignById(@Param("id") Long id, @Param("source") String source);
}

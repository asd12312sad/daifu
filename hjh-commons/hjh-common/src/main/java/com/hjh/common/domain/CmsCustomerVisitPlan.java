package com.hjh.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hjh.common.annotation.Excel;
import com.hjh.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 客户跟访记录对象 cms_customer_visit
 *
 * @author xiaobing
 * @date 2021-08-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CmsCustomerVisitPlan extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 客户ID
     */
    @Excel(name = "客户ID", readConverterExp = "客户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;

    /**
     * 关联ID
     */
    @Excel(name = "关联ID", readConverterExp = "关联ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relevanceId;

    /**
     * 操作类型
     */
    @Excel(name = "操作类型", readConverterExp = "操作类型")
    private String actionType;

    /**
     * 计划跟访时间
     */
    @Excel(name = "计划跟访时间", readConverterExp = "计划跟访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date plannedVisitTime;

    /**
     * 计划跟访备注
     */
    @Excel(name = "计划跟访备注", readConverterExp = "计划跟访备注")
    private String plannedVisitRemark;

    /**
     * 跟访状态
     */
    @Excel(name = "跟访状态", readConverterExp = "跟访状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitStatus;

    /**
     * 跟访时间
     */
    @Excel(name = "跟访时间", readConverterExp = "跟访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date visitTime;

    /**
     * 跟访人
     */
    @Excel(name = "跟访人", readConverterExp = "跟访人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitUserId;

    /**
     * 最新跟访人姓名
     */
    @Excel(name = "最新跟访人姓名", readConverterExp = "最新跟访人姓名")
    private String visitUserName;

    /**
     * 状态
     */
    @Excel(name = "状态", readConverterExp = "状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long status;

    private String deptName;


}

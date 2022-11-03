package com.hjh.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hjh.common.annotation.Excel;
import com.hjh.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 客户每日汇总对象 cms_customer_daily_assign
 *
 * @author xiaobing
 * @date 2021-08-24
 */
@Data
public class CmsCustomerDailyAssignSource extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 状态 0 未分配 1 部分分配 2 已分配
     */
    @Excel(name = "状态 0 未分配 1 部分分配 2 已分配")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long status;

    /**
     * 已分配数量
     */
    @Excel(name = "已分配数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assignedCount;

    /**
     * 当日客户数量
     */
    @Excel(name = "当日客户数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long allCount;


    /**
     * 最新分配时间
     */
    @Excel(name = "最新分配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastAssignTime;

    /**
     * 新增客户时间
     */
    @Excel(name = "新增客户时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createCustomerDate;

    /**
     * 最后分配人
     */
    @Excel(name = "最后分配人")
    private String lastAssignBy;

    /**
     * 最后分配人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastAssignById;

    private String dateStr;

    private String source;
    private Long dailyAssignId;

    /**
     * 客户分配信息
     */
    @TableField(exist = false)
    private List<CmsCustomerAssign> cmsCustomerAssignList;


}

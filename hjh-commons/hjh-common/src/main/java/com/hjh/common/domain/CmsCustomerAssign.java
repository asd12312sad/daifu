package com.hjh.common.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.hjh.common.annotation.Excel;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 客户分配对象 cms_customer_assign
 *
 * @author xiaobing
 * @date 2021-08-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CmsCustomerAssign extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 客户每日分配ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dailyAssignId;

    /**
     * 分配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "分配时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;

    /**
     * 分配人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assignUserId;

    /**
     * 被分配人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long beAssignedUserId;

    /**
     * 分配人
     */
    @Excel(name = "分配人")
    private String assignUser;

    /**
     * 被分配人
     */
    @Excel(name = "被分配人")
    private String beAssignedUser;

    /**
     * 分配数量
     */
    @Excel(name = "分配数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assignCount;

    private String source;

}

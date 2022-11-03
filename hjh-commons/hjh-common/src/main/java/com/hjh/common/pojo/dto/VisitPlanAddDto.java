package com.hjh.common.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/24 16:36
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/24 16:36        肖兵           v1.0.0           Created
 */
@Data
public class VisitPlanAddDto {

    /**
     * 客户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;

    /**
     * 计划跟访时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date plannedVisitTime;

    /**
     * 计划跟访备注
     */
    private String plannedVisitRemark;

    /**
     * 跟访状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitStatus;

    /**
     * 跟访人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitUserId;

    /**
     * 备注
     */
    private String remark;
}

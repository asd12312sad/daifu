package com.hjh.common.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 分配统计数据
 *
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/25 15:34
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/25 15:34        肖兵           v1.0.0           Created
 */
@Data
public class DistributionStatisticsDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String deptName;

    private String sex;

    private String sourceId;

    private Integer deptId;

    private String startDate;

    private String endDate;

    private String searchValue;


}

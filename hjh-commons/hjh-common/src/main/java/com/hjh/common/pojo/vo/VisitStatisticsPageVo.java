package com.hjh.common.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hjh.common.annotation.Excel;
import lombok.Data;

/**
 * 跟访统计
 *
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/26 14:29
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/26 14:29        肖兵           v1.0.0           Created
 */

@Data
public class VisitStatisticsPageVo {


    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 员工名称
     */
    @Excel(name = "员工名称")
    private String userName;

    /**
     * 员工性别
     */
    @Excel(name = "员工性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    private String deptName;
    /**
     * 跟访数量
     */
    @Excel(name = "跟访数量")
    private Integer visitCount;

    /**
     * 延时数量
     */
    private Integer delayVisitCount;

    /**
     * 延时率
     */
    @Excel(name = "延时率")
    private String delayVisitProportion;
    /**
     * 最大延时时间
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long maxDelayTime;

    /**
     * 最大延时时间
     */
    @Excel(name = "最大延时时间")
    private String maxDelayTimeStr;
}

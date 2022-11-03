package com.hjh.common.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hjh.common.annotation.Excel;
import lombok.Data;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/25 17:58
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/25 17:58        肖兵           v1.0.0           Created
 */
@Data
public class StatisticsPageVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    @Excel(name = "员工姓名")
    private String userName;

    @Excel(name = "员工性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    @Excel(name = "部门名称")
    private String deptName;

    /**
     * 新增数量
     */
    @Excel(name = "新增数量")
    private Integer addCount;
    /**
     * 新增数量
     */
    @Excel(name = "总数量")
    private Integer allCount;
    /**
     * 回收数量
     */
    @Excel(name = "回收数量")
    private Integer recycleCount;

    /**
     * 签单数量
     */
    @Excel(name = "签单数量")
    private Integer signCount;
}

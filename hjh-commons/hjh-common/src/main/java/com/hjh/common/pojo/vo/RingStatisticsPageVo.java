package com.hjh.common.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hjh.common.annotation.Excel;
import lombok.Data;

/**
 * 擂台排名
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
public class RingStatisticsPageVo {


    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "用户序号", cellType = Excel.ColumnType.NUMERIC, prompt = "用户编号")
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
     * 分配数量
     */
    @Excel(name = "分配数量", cellType = Excel.ColumnType.NUMERIC, prompt = "分配数量")
    private Integer allCount;

    /**
     * 有效数量
     */
    @Excel(name = "有效数量", cellType = Excel.ColumnType.NUMERIC, prompt = "有效数量")
    private Integer effectiveCount;
    /**
     * 邀约数量
     */
    @Excel(name = "邀约数量", cellType = Excel.ColumnType.NUMERIC, prompt = "邀约数量")
    private Integer inviteCount;

    /**
     * 到店数量
     */
    @Excel(name = "到店数量", cellType = Excel.ColumnType.NUMERIC, prompt = "到店数量")
    private Integer toShopCount;


    /**
     * 有效率
     */
    @Excel(name = "有效率")
    private String effectiveProportion;
    /**
     * 邀约率
     */
    @Excel(name = "邀约率")
    private String inviteProportion;

    /**
     * 到店率
     */
    @Excel(name = "到店率")
    private String toShopProportion;

    /**
     * 到店占比
     */
    @Excel(name = "到店占比")
    private String toShopPercentage;


}

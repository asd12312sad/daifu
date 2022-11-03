package com.hjh.erp.domain;

import java.math.BigDecimal;

import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 游戏对象 game
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Data
public class Game {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    /** ID */
    private Long id;

    /**
     * 游戏名称
     */
    @Excel(name = "游戏名称")
    private String name;

    private BigDecimal odds;

    /**
     * 官方投注地址
     */
    @Excel(name = "官方投注地址")
    private String address;

    /**
     * u盾KEY
     */
    @Excel(name = "u盾KEY")
    private String udunKey;

    /**
     * 最小投注金额
     */
    @Excel(name = "最小投注金额")
    private BigDecimal minAmount;

    /**
     * 最大投注金额
     */
    @Excel(name = "最大投注金额")
    private BigDecimal maxAmount;

    /**
     * 排序
     */
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private Integer status;

    /**
     * 盈利手续费
     */
    private BigDecimal profitFee;

    /**
     * 违规手续费
     */
    private BigDecimal violationFee;

    /**
     * 赔率-闲家
     */
    private BigDecimal oddsIdle;

    /**
     * 赔率-和
     */
    private BigDecimal oddsSum;

}

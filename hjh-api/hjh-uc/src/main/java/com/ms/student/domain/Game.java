package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

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
    private Integer id;

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 官方投注地址
     */
    private String address;

    @TableField(exist = false)
    private String contractAddress;

    /**
     * u盾KEY
     */
    private String udunKey;

    private BigDecimal odds;

    /**
     * 最小投注金额
     */
    private BigDecimal minAmount;

    /**
     * 最大投注金额
     */
    private BigDecimal maxAmount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
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

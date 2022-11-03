package com.hjh.erp.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 下注记录对象 order
 *
 * @author xiaobing
 * @date 2022-04-15
 */
@Data
@TableName("`order`")
public class Order extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    /** 表ID */
    private Long id;

    /**
     * 玩家名称
     */
    @Excel(name = "玩家名称")
    private String userName;

    /**
     * 付款者地址
     */
    @Excel(name = "付款者地址")
    private String payAddress;

    /**
     * 游戏名称
     */
    @Excel(name = "游戏名称")
    private String gameName;

    /**
     * 下单状态
     */
    @Excel(name = "下单状态")
    private Integer status;

    /**
     * 结果
     */
    @Excel(name = "结果")
    private Integer result;

    /**
     * 玩家结果
     */
    @Excel(name = "玩家结果")
    private String userResult;

    /**
     * 系统结果
     */
    @Excel(name = "系统结果")
    private String systemResult;

    /**
     * 充币数量
     */
    @Excel(name = "充币数量")
    private BigDecimal amount;

    /**
     * 一级代理分红
     */
    @Excel(name = "一级代理分红")
    private BigDecimal proxyOneAmount;

    /**
     * 二级代理分红
     */
    @Excel(name = "二级代理分红")
    private BigDecimal proxyTwoAmount;

    /**
     * 反水金额
     */
    @Excel(name = "反水金额")
    private BigDecimal fanshuiAmount;

    /**
     * U盾手续费
     */
    @Excel(name = "U盾手续费")
    private BigDecimal fee;

    /**
     * 充币地址
     */
    @Excel(name = "充币地址")
    private String address;

    /**
     * 交易HASH
     */
    @Excel(name = "交易HASH")
    private String txId;

    /**
     * u盾订单号
     */
    private String tradeId;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 游戏ID
     */
    private Integer gameId;

    /**
     * 玩家ID
     */
    private Integer userId;

    /**
     * 玩家一级代理ID
     */
    private Integer proxyOneId;

    /**
     * 玩家二级代理ID
     */
    private Integer proxyTwoId;

    /**
     * 反水状态
     */
    @Excel(name = "反水状态")
    private Integer fanshui;

    /**
     * 一级代理名称
     */
    @Excel(name = "一级代理名称")
    private String proxyOneName;

    /**
     * 二级代理名称
     */
    @Excel(name = "二级代理名称")
    private String proxyTwoName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;

    private String remark;
    private BigDecimal profit;


    /**
     * 推广返佣状态
     */
    private Integer tuiguangOne;
    private Integer tuiguangTwo;

}

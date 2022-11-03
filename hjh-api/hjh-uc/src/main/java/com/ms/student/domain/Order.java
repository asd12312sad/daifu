package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 下注记录对象 order
 *
 * @author xiaobing
 * @date 2022-04-15
 */
@Data
@TableName("`order`")
public class Order  {
    private static final long serialVersionUID = 1L;
    /** 表ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 玩家名称
     */
    private String userName;

    /**
     * 付款者地址
     */
    private String payAddress;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 下单状态
     */
    @TableField(value="`status`")
    private Integer status;

    /**
     * 结果
     */
    @TableField(value="`result`")
    private Integer result;

    /**
     * 玩家结果
     */
    private String userResult;

    /**
     * 系统结果
     */
    private String systemResult;

    /**
     * 充币数量
     */
    private BigDecimal amount;

    /**
     * 一级代理分红
     */
    private BigDecimal proxyOneAmount;


    private Integer type;

    /**
     * 二级代理分红
     */
    private BigDecimal proxyTwoAmount;

    /**
     * 反水金额
     */
    private BigDecimal fanshuiAmount;

    /**
     * U盾手续费
     */
    private BigDecimal fee;

    /**
     * 充币地址
     */
    private String address;

    /**
     * 交易HASH
     */
    private String txId;

    /**
     * u盾订单号
     */
    private String tradeId;

    /**
     * 交易类型
     */
    private Integer tradeType;

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
    private Integer fanshui;

    /**
     * 推广返佣状态
     */
    private Integer tuiguangOne;
    private Integer tuiguangTwo;

    /**
     * 一级代理名称
     */
    private String proxyOneName;

    /**
     * 二级代理名称
     */
    private String proxyTwoName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;

    private String remark;

    private BigDecimal profit;



}

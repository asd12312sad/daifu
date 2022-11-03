package com.hjh.erp.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 代理对象 agent
 *
 * @author xiaobing
 * @date 2022-07-09 22:39:14
 */
@Data
public class Agent extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 账号
     */
    @Excel(name = "账号")
    private String account;

    /**
     * 密码
     */
    private String password;
    private BigDecimal balance;

    /**
     * 登录次数
     */
    @Excel(name = "登录次数")
    private Integer loginCount;

    /**
     * 最后登录日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后登录日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginDate;

    /**
     * 最后登录IP
     */
    @Excel(name = "最后登录IP")
    private String ip;

    /**
     * 最低代付手续费率
     */
    @Excel(name = "最低代付手续费率")
    private BigDecimal payFee;

    /**
     * 最低代收手续费率
     */
    @Excel(name = "最低代收手续费率")
    private BigDecimal fee;

    private String inviteCode;






    private Date createTime;


    private String remark;


    /**
     * 一结算金额
     */
    private BigDecimal withdrawAmount;

    private BigDecimal unbalancedAmount;


    /**
     * usdt 结算地址
     */
    private String usdtAddress;

    private String googleSignCode;


    @TableField(exist = false)
    private String adminGoogleSign;


}

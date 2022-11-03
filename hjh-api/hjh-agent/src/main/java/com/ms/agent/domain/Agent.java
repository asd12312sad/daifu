package com.ms.agent.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Agent {

    private Long id;

    private String name;

    private String account;

    private String password;

    private String createBy;


    private BigDecimal balance;
    private String updateBy;

    private Date createTime;

    private Date updateTime;

    private String remark;

    private Integer loginCount;

    private String ip;

    private Date lastLoginDate;

    private BigDecimal payFee;

    private BigDecimal fee;

    /**
     * 一结算金额
     */
    private BigDecimal withdrawAmount;

    /**
     * usdt 结算地址
     */
    private String usdtAddress;

    private String googleSignCode;


    private BigDecimal unbalancedAmount;
    private String inviteCode;


}

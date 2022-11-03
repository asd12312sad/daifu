package com.ms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户管理对象 merchant
 *
 * @author xiaobing
 * @date 2022-06-02
 */
@Data
public class Merchant {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    /** id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 手续费
     */
    private BigDecimal fee;

    private BigDecimal payFee;
    /**
     * 试用额度
     */
    private Integer trialQuota;

    private Integer status;

    private String returnAddress;


    private String trxAddress;

    private String usdtAddress;

    private String returnPayAddress;

    private String usdtPayAddress;

    private String usdtPayPrivateKey;
    private String inviteCode;


    private String trxPrivateKey;

    private String rechargeAddress;

    private String rechargePrivateKey;

    private BigDecimal trxBalance;
    private String sign;

    @TableField(exist = false)
    private BigDecimal usdtBalance;
    @TableField(exist = false)
    private BigDecimal usdtPayTrxBalance;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String account;

    private String password;

    private String googleSignCode;

    private Date lastLoginDate;
    private Integer loginCount;
    private String ip;
    private String telegramBotToken;

    private String telegramId;
    private Integer agentId;

    @TableField(exist = false)
    private String googleSign;


}

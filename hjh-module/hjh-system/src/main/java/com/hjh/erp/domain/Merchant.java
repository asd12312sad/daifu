package com.hjh.erp.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 商户管理对象 merchant
 *
 * @author xiaobing
 * @date 2022-06-02
 */
@Data
public class Merchant extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    /** id */
    private Long id;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 余额
     */
    @Excel(name = "余额")
    private BigDecimal balance;

    /**
     * 手续费
     */
    @Excel(name = "手续费")
    private BigDecimal fee;


    private BigDecimal payFee;

    private String returnAddress;

    /**
     * 试用额度
     */
    private Integer trialQuota;

    private String trxAddress;

    private String usdtAddress;


    private String usdtPayAddress;

    private String usdtPayPrivateKey;

    private String rechargeAddress;

    private String rechargePrivateKey;



    private String trxPrivateKey;



   private BigDecimal trxBalance;
    private String sign;

    @TableField(exist = false)
    private BigDecimal usdtBalance;
    @TableField(exist = false)
    private BigDecimal usdtPayTrxBalance;


    private String account;

    private String password;

    private String googleSignCode;

    private String email;


    private Date lastLoginDate;

    private String telegramBotToken;

    private String telegramId;

    @TableField(exist = false)
    private String agentName;

}

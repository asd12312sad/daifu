package com.ms.merchant.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商户管理对象 merchant
 *
 * @author xiaobing
 * @date 2022-06-02
 */
@Data
public class Merchant  {
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

    private Integer agentId;

    /**
     * 试用额度
     */
    private Integer trialQuota;

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
    @TableField(exist = false)
    private List<MerchantMenu> merchantMenuList;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    private String account;

    private String password;

    private String googleSignCode;

    private Date lastLoginDate;
    private Integer loginCount;
    private String ip;

    private String telegramBotToken;

    private String telegramId;

    /**
     * 代收APi白名单
     */
    private String collectionApiWhile;

    /**
     * 代付APi白名单
     */
    private String payApiWhile;

    /**
     * 赠送余额
     */
    private BigDecimal presentedBalance;

    private String email;

    private BigDecimal notionalPoolingAmount;

}

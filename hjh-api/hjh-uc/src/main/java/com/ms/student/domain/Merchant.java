package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户管理对象 merchant
 *
 * @author xiaobing
 * @date 2022-06-02
 */
@Data
public class Merchant {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    /** id */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 余额
     */
    private BigDecimal balance;
    private BigDecimal feeAmount;


    private String usdtAddress;
    /**
     * 手续费
     */
    private BigDecimal fee;
    private BigDecimal payFee;

    private String returnAddress;

    private String  returnPayAddress;


    private String trxAddress;


    private String rechargeAddress;

    private String rechargePrivateKey;
    private String trxPrivateKey;


    private String trxBalance;


    private String sign;

    private String usdtPayAddress;

    private String usdtPayPrivateKey;

    /**
     * 试用额度 判断是否大于0 如果大于0的话 setBalance 的地方把这个值-1 不进行setBalance 操作
     * 如果小于0 则进行setBalance 操作 不进行-1
     */
    private Integer trialQuota;

    private String telegramBotToken;

    private String telegramId;

    /**
     * 代收APi白名单
     */
    private String collectionApiWhile;

    /**
     * 赠送余额
     */
    private BigDecimal presentedBalance;

    /**
     * 代付APi白名单
     */
    private String payApiWhile;

    private Integer agentId;


    @TableField(exist = false)
    private String agentName;

    private BigDecimal notionalPoolingAmount;
}

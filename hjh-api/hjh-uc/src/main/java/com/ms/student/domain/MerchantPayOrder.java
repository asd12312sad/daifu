package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户付款地址对象 merchant_pay_address
 *
 * @author ruoyi
 * @date 2022-06-02
 */
@Data
public class MerchantPayOrder {
    private static final long serialVersionUID = 1L;
    /** 收款地址 */
    @TableId
    private Long id;

    private String address;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * hex_address
     */
    private String hexAddress;

    /**
     * 回调地址
     */
    private String returnAddress;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * private_key_base
     */
    private String privateKeyBase;

    /**
     * tex余额
     */
    private BigDecimal trxBalance;

    /**
     * usdt余额
     */
    private BigDecimal usdtBalance;

    /**
     * 商户订单号
     */
    private String merchantOrderNo;

    private String payAddress;

    private Integer haveUsdt;

    private Date createTime;

    private String orderSn;

    /**
     * 支付状态 0 支付失败 1 支付中 2 支付到账 3 队列中 4 待处理
     */
    private Integer status;

    /**
     * 支付结果
     */
    private String payResult;

    /**
     * 支付hash
     */
    private String txid;

    private Integer agentId;

    private String message;

    private String ownerAddress;

    private String returnMsg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returnTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date successTime;


    private String requestLog;

    private String responseLog;

    private String returnRequestLog;

    private String returnResponseLog;

    private BigDecimal feeAmount;


    private BigDecimal agentFee;


    private String params;

}

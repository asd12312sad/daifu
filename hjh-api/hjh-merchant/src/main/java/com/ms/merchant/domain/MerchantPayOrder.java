package com.ms.merchant.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    /**
     * 收款地址
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String message;

    private String ownerAddress;

    private String returnMsg;

    /**
     * 0 支付失败 1 支付中 2 支付到账 3 队列中
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

    @TableField(exist = false)
    private String startDate;
    @TableField(exist = false)
    private String endDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returnTime;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long auditId;
    @TableField(exist = false)
    private Integer type;

    @TableField(exist = false)
    private Integer auditStatus;

    @TableField(exist = false)
    private String refuseMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date successTime;

    private String requestLog;

    private String responseLog;

    /**
     * 手续费金额
     */
    private BigDecimal feeAmount;


    private String returnRequestLog;

    private String returnResponseLog;

    private String orderSn;



}

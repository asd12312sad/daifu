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
public class MerchantPayAddress {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 收款地址 */
    private String address;

    /**
     * 商户ID
     */
    private String merchantId;

    private Integer agentId;


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

    private String orderSn;

    private String payAddress;

    private Integer haveUsdt;

    private Date createTime;

    private String message;


    /**
     * 状态 0:未支付 1:支付成功 2:支付金额不匹配  3:商户手续费不足 4:商户TRX不足无法归集 5：回调失败 6: 支付过程错误 7：逾期未支付
     */
    private Integer status;

    /**
     * 支付者地址
     */
    private String ownerAddress;


    private String returnMsg;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal receivedAmount;

    /**
     * 收集剩余trx
     */
    private Integer gather;

    /**
     * 手续费金额
     */
    private BigDecimal feeAmount;


    /**
     * 代付结果
     */
    private String payResult;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returnTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date successTime;

    private String requestLog;

    private String responseLog;

    private String returnRequestLog;

    private String returnResponseLog;

    private BigDecimal agentFee;

    private String backUrl;
    private String txId;


    private String params;
}

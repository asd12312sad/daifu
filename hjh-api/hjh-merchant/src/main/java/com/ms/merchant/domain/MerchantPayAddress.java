package com.ms.merchant.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * 商户付款地址对象 merchant_pay_address
 *
 * @author ruoyi
 * @date 2022-06-02
 */
@Data
public class MerchantPayAddress {
    private static final long serialVersionUID = 1L;

    @TableId(type=IdType.ASSIGN_UUID)
    private String id;

    @Excel(name = "收款地址")
    private String address;

    /**
     * 商户ID
     */
    @Excel(name = "商户ID")
    private String merchantId;

    /**
     * hex_address
     */
    private String hexAddress;

    /**
     * 回调地址
     */
    @Excel(name = "回调地址")
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
    @Excel(name = "trx余额")
    private BigDecimal trxBalance;

    /**
     * usdt余额
     */
    @Excel(name = "usdt余额")
    private BigDecimal usdtBalance;

    /**
     * 商户订单号
     */
    @Excel(name = "订单号")
    private String merchantOrderNo;

    @Excel(name = "付款地址")
    private String payAddress;

    private Integer haveUsdt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @Excel(name = "回调结果")
    private String message;


    @Excel(name = "归集地址")
    private String ownerAddress;

    private String returnMsg;
    /**
     * 状态 0:未支付 1:支付成功 2:支付金额不匹配  3:商户手续费不足 4:商户TRX不足无法归集 5：回调失败 6: 支付过程错误
     */
    private Integer status;

    private BigDecimal orderAmount;

    private BigDecimal receivedAmount;


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

    /**
     * 手续费金额
     */
    private BigDecimal feeAmount;



    @TableField(exist = false)
    private Integer auditStatus;

    @TableField(exist = false)
    private String refuseMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date successTime;

    private String requestLog;

    private String responseLog;

    private String returnRequestLog;

    private String returnResponseLog;

    private String orderSn;

    /**
     * 代付结果
     */
    private String payResult;

    private BigDecimal agentFee;

    private String backUrl;


    private String params;
}

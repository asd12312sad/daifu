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

/**
 * 商户代付审核对象 merchant_pay_order_audit
 *
 * @author ruoyi
 * @date 2022-06-02
 */
@Data
public class MerchantPayOrderAudit {
    private static final long serialVersionUID = 1L;
    /** 收款地址 */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long id;

    //类型 1 大额 2 余额不足
    private Integer type;
    private String address;


    /**
     * 回调状态 0 未回调 1 回调成功 2 回调失败
     */
    @TableField(exist = false)
    private Integer returnStatus;
    private String returnMsg;
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long payOrderId;
    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 回调地址
     */
    private String returnAddress;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * usdt余额
     */
    private BigDecimal usdtBalance;

    /**
     * 商户订单号
     */
    private String merchantOrderNo;

    /**
     * 支付状态 0 未审核 1 已审核 2 拒绝 3 支付失败 4 支付中 5 队列中
     */
    private Integer status;

    /**
     * 收款地址
     */
    private String ownerAddress;


    /**
     * 手续费金额
     */
    private BigDecimal feeAmount;

    private String refuseMsg;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private Integer haveUsdt;

    @TableField(exist = false)
    private String startDate;
    @TableField(exist = false)
    private String endDate;
    @TableField(exist = false)
    private String message;


}

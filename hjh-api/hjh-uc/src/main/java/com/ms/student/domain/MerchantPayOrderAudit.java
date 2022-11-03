package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
    private Long id;

    private String address;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 类型 1 大额 2 余额不足 3 代付异常订单
     */
    private Integer type;

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
     * 支付状态 0 未审核 1 已审核 2 拒绝
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

    /**
     * 创建时间
     */
    private Date createTime;

}

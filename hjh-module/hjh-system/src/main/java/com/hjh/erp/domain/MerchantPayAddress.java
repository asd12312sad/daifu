package com.hjh.erp.domain;

import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 商户付款地址对象 merchant_pay_address
 *
 * @author ruoyi
 * @date 2022-06-03
 */
@Data
public class MerchantPayAddress extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 收款地址 */
    @TableId(type = IdType.ASSIGN_ID)
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

    private String message;

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
    @Excel(name = "tex余额")
    private String trxBalance;

    /**
     * usdt余额
     */
    @Excel(name = "usdt余额")
    private String usdtBalance;

    /**
     * 商户订单号
     */
    @Excel(name = "商户订单号")
    private String merchantOrderNo;
    private String returnMsg;


}

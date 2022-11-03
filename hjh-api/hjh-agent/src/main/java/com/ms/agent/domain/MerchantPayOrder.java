package com.ms.agent.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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

    @Excel(name = "收款地址")
    private String address;

    /**
     * 商户ID
     */
    @Excel(name = "商户ID")
    private String merchantId;

    private Integer agentId;

    @TableField(exist = false)
    @Excel(name = "商户名称")
    private String merchantName;

    @Excel(name = "商户账号")
    @TableField(exist = false)
    private String merchantAccount;
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
    private BigDecimal trxBalance;

    /**
     * usdt余额
     */
    @Excel(name = "订单金额")
    private BigDecimal usdtBalance;

    /**
     * 商户订单号
     */
    @Excel(name = "商户订单号")
    private String merchantOrderNo;

    @Excel(name = "付款地址")
    private String payAddress;

    @Excel(name = "付款是否成功", replace = { "成功_1", "失败_0" })
    private Integer haveUsdt;

    @Excel(name = "创建实际")
    private Date createTime;

    /**
     * 支付状态 0 支付失败 1 支付中 2 支付到账 3 队列中
     */
    @Excel(name = "状态", replace = { "支付失败_0", "支付中_1", "支付到账_2" ,"队列中_3"})
    private Integer status;

    /**
     * 支付结果
     */
    @Excel(name = "支付结果")
    private String payResult;

    /**
     * 支付hash
     */
    @Excel(name = "支付hash")
    private String txid;

    @Excel(name = "消息")
    private String message;

    private String ownerAddress;

    private String returnMsg;

    @TableField(exist = false)
    private String startDate;
    @TableField(exist = false)
    private String endDate;


}

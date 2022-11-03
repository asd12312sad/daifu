package com.ms.agent.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
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
public class MerchantPayAddress {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    /** 收款地址 */
    @Excel(name = "收款地址")
    private String address;

    /**
     * 商户ID
     */
    @Excel(name = "商户ID")
    private String merchantId;

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
    @Excel(name = "商户订单号")
    private String merchantOrderNo;

    @Excel(name = "支付地址")
    private String payAddress;

    @Excel(name = "是否收到款", replace = { "收到_1", "未收到_0" })
    private Integer haveUsdt;

    @Excel(name = "创建订单时间")
    private Date createTime;

    @Excel(name = "回调结果")
    private String message;


    /**
     * 状态 0:未支付 1:支付成功 2:支付金额不匹配  3:商户手续费不足 4:商户TRX不足无法归集 5：回调失败 6: 支付过程错误 7：逾期未支付
     */
    @Excel(name = "状态", replace = { "未支付_0", "支付成功_1", "支付金额不匹配_2", "商户手续费不足_3", "商户TRX不足无法归集_4", "回调失败_5", "支付过程错误_6", "逾期未支付_7" })
    private Integer status;

    @Excel(name = "归集地址")
    private String ownerAddress;


    private String returnMsg;

    @Excel(name = "订单金额")
    private BigDecimal orderAmount;

    @Excel(name = "实际收款金额")
    private BigDecimal receivedAmount;

    /**
     * 收集剩余trx
     */
    private Integer gather;

    /**
     * 手续费金额
     */
    @Excel(name = "手续费金额")
    private BigDecimal feeAmount;


    private Integer agentId;

    @TableField(exist = false)
    private String startDate;
    @TableField(exist = false)
    private String endDate;

}

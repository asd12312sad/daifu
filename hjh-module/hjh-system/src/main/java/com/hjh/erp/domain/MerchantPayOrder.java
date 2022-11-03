package com.hjh.erp.domain;

import java.math.BigDecimal;
import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 商户代付记录对象 merchant_pay_order
 *
 * @author xiaobing
 * @date 2022-06-10
 */
@Data
public class MerchantPayOrder extends BaseEntity
{
    @TableId
    private Long id;

    private static final long serialVersionUID = 1L;
    /** 付款地址 */
            @Excel(name = "付款地址")
    private String address;

    /** 商户ID */
            @Excel(name = "商户ID")
    private String merchantId;

    /** hex_address */
    private String hexAddress;

    /** 回调地址 */
            @Excel(name = "回调地址")
    private String returnAddress;

    /** 私钥 */
    private String privateKey;

    /** private_key_base */
    private String privateKeyBase;

    /** tex余额 */
            @Excel(name = "tex余额")
    private BigDecimal trxBalance;

    /** usdt余额 */
            @Excel(name = "usdt余额")
    private BigDecimal usdtBalance;

    /** 商户订单号 */
            @Excel(name = "商户订单号")
    private String merchantOrderNo;

    /** 是否已经收到款 */
            @Excel(name = "是否已经收到款")
    private Integer haveUsdt;

    /** 支付者地址 */
            @Excel(name = "支付者地址")
    private String payAddress;

    /** 订单号 */
            @Excel(name = "订单号")
    private String orderNo;

    /** 日志记录 */
            @Excel(name = "日志记录")
    private String message;

    /** 收款地址 */
            @Excel(name = "收款地址")
    private String ownerAddress;


    private String returnMsg;

}

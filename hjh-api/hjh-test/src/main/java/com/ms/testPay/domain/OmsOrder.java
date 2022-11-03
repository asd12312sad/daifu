package com.ms.testPay.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsOrder
{
    private Long id;

    private String orderSn;

    private String address;
    private String ownerAddress;


    private BigDecimal amount;

    private BigDecimal realAmount;


    /**
     * 0 未支付 1支付中 2 支付成功 3 支付失败
     */
    private Integer status;

    private String result;

    private String asynResult;

    /**
     * 1 代收 2 代付
     */
    private String orderType;

}

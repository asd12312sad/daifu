package com.ms.agent.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

//成功请求金额：479,036
//成功支付金额：479,036
//手续费：:20,765
//成功订单：191
//总订单；335
//转化率； 57.01%
@Data
public class OrderHeaderVo {

    private BigDecimal askSuccessAmount;

    private BigDecimal paySuccessAmount;

    private BigDecimal feeAmount;

    private Integer paySuccessCount;

    private Integer orderCount;




}

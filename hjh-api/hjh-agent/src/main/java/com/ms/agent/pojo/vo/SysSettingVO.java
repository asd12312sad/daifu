package com.ms.agent.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysSettingVO {

private String usdtAddress;

    /**
     * 利润总计
     */
private BigDecimal profitAmount;

    /**
     * 代付利润
     */
private BigDecimal payAmount;

    /**
     * 代收利润
     */
private BigDecimal collectAmount;


    /**
     * 未结算金额
     */
private BigDecimal  outstandingAmount;


    /**
     * 已结算金额
     */
private BigDecimal settledAmount;
}

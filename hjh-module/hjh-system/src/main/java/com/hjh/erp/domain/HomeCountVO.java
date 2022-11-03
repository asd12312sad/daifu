package com.hjh.erp.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeCountVO {

private Integer payCount;

private BigDecimal paySumAmount;

private Integer payEndCount;

private Integer payReturnCount;

    private Integer collectCount;

    private BigDecimal collectSumAmount;

    private Integer collectEndCount;

    private Integer collectReturnCount;

 }

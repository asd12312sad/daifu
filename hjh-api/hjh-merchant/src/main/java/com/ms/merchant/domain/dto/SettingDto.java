package com.ms.merchant.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettingDto {


    private String usdtAddress;

    private String returnAddress;

    private String returnPayAddress;

    private String googleSign;


    /**
     * 代收APi白名单
     */
    private String collectionApiWhile;

    /**
     * 代付APi白名单
     */
    private String payApiWhile;

    private String telegramBotToken;

    private BigDecimal notionalPoolingAmount;

}

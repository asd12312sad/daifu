package com.ms.student.domain.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Data
public class CreateAddressDto {


  private   String merchantId;

 private    BigDecimal amount;
 private String sign;
private String orderNo;
private String returnAddress;
private String ownerAddress;
private String backUrl;

    private String params;

}

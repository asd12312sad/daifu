package com.ms.student.domain.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Data
public class CreateOrderDto {


  private String merchantAddress;;
  private String merchantId;
  private String sign;
  private String orderNo;
  private String returnAddress;
  private String payAddress;
  private BigDecimal amount;

  private String params;

}

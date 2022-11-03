package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 管理员分红记录
 * </p>
 *
 * @author Mazhaoyang
 * @since 2021-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_recharge")
public class LogRecharge implements Serializable {

  private static final long serialVersionUID=1L;

  private Long id;

  private Long uid;

  private BigDecimal amount;

  private Date createdate;

  private String txId;

}

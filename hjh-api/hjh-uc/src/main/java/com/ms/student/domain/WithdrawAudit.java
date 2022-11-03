package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WithdrawAudit {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;

    /**
     * 提币地址
     */
    private String address;


    /**
     * 交易号
     */
    private String txId;

    /**
     * 状态 0 待审核 1 审核通过 2 审核不通过 3 提币成功 4 提币失败
     */
    private Integer status;

    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 提币数量
     */
    private BigDecimal amount;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 类型 4反水 5佣金
     */
    private Integer type;

}

package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MerchantPayUseAddress {

    private Integer id;

    private Long merchantId;

    private String address;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * private_key_base
     */
    private String privateKeyBase;

    /**
     * hex_address
     */
    private String hexAddress;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private BigDecimal trxBalance;
    @TableField(exist = false)
    private BigDecimal usdtBalance;




}

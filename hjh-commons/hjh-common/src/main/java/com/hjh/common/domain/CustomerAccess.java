package com.hjh.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hjh.common.annotation.Excel;
import com.hjh.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户对象 cms_customer
 *
 * @author xiaobing
 * @date 2021-08-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "cms_customer_access")
public class CustomerAccess extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名", readConverterExp = "客户姓名")
    private String name;


    /**
     * 联系电话
     */
    @Excel(name = "联系电话", readConverterExp = "联系电话")
    private String linkPhone;
    /**
     * 客户ID
     */
    @Excel(name = "客户ID", readConverterExp = "客户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;


}

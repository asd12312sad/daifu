package com.hjh.common.domain;

import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 客户关联文件对象 cms_customer_file
 *
 * @author xiaobing
 * @date 2021-08-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CmsCustomerFile extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 客户ID
     */
    @Excel(name = "客户ID", readConverterExp = "客户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;

    /**
     * 关联ID
     */
    @Excel(name = "关联ID", readConverterExp = "关联ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relevanceId;

    /**
     * 文件名称
     */
    @Excel(name = "文件名称", readConverterExp = "文件名称")
    private String name;

    /**
     * 文件大小
     */
    @Excel(name = "文件大小", readConverterExp = "文件大小")
    private String fileSize;

    /**
     * 文件类型
     */
    @Excel(name = "文件类型", readConverterExp = "文件类型")
    private String type;

    /**
     * 链接地址
     */
    @Excel(name = "链接地址", readConverterExp = "链接地址")
    private String url;

    /**
     * 文件上传人ID
     */
    @Excel(name = "文件上传人ID", readConverterExp = "文件上传人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pudUserId;

    /**
     * 状态
     */
    @Excel(name = "状态", readConverterExp = "状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long status;


}

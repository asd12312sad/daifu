package com.ms.common.core.domain;

import lombok.Data;

import java.util.Date;

/**
 * Entity基类
 *
 * @author xiaobing
 */
@Data
public class BaseEntity {

    /**
     * 创建时间
     */
    private Date createDate;


    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 创建时间
     */
    private String createBy;


    /**
     * 修改时间
     */
    private String updateBy;
}

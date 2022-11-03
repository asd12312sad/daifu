package com.ms.common.core.domain;

import lombok.Data;

/**
 * 搜索Entity基类
 *
 * @author xiaobing
 */
@Data
public class BaseSearchEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索值
     */
    private String searchValue;

    /**
     * 每页显示数量
     */
    private Integer pageSize;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    private String isAsc;
}

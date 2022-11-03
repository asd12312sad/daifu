package com.hjh.erp.domain;

import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 轮播图对象 banner
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Data
public class Banner extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    /** id */
    private Long id;

    /**
     * 图片
     */
    @Excel(name = "图片")
    private String img;

    /**
     * 跳转地址
     */
    @Excel(name = "跳转地址")
    private String url;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private Integer status;

    /**
     * 排序
     */
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 搜索
     */
    @Excel(name = "搜索")
    private String searchValue;


}

package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 轮播图对象 banner
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Data
public class Banner  {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    /** id */
    private Long id;

    /**
     * 图片
     */
    private String img;

    /**
     * 跳转地址
     */
    private String url;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 搜索
     */
    private String searchValue;


}

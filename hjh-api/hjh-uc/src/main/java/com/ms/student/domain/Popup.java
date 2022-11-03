package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 弹窗对象 popup
 *
 * @author xiaobing
 * @date 2022-04-22 06:14:52
 */
@Data
public class Popup {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 按钮标题
     */
    private String buttonTitle;

    /**
     * 按钮跳转地址
     */
    private String buttonHerf;

    /**
     * 按钮是否启用
     */
    private Integer buttonFlag;

    /**
     * 弹窗是否启用
     */
    private Integer popupFlag;

    /**
     * 语言
     */
    private String lang;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


}

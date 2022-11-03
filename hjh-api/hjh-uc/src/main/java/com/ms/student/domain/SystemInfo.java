package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 系统日志对象 system_info
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Data
public class SystemInfo {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 电报
     */
    private String telegram;

    private String kefu;

    /**
     * whatsapp
     */
    private String whatsapp;

    /**
     * 返水百分比
     */
    private BigDecimal backwater;

    /**
     * 一级代理百分比
     */
    private BigDecimal oneAgent;

    /**
     * 二级代理百分比
     */
    private BigDecimal twoAgent;


    /**
     * 最低提币
     */
    private BigDecimal minWithdrawal;



}

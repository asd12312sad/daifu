package com.hjh.erp.domain;

import java.math.BigDecimal;

import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

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
    @Excel(name = "电报")
    private String telegram;

    private String kefu;

    /**
     * whatsapp
     */
    @Excel(name = "whatsapp")
    private String whatsapp;

    /**
     * 返水百分比
     */
    @Excel(name = "返水百分比")
    private BigDecimal backwater;

    /**
     * 一级代理百分比
     */
    @Excel(name = "一级代理百分比")
    private BigDecimal oneAgent;

    /**
     * 二级代理百分比
     */
    @Excel(name = "二级代理百分比")
    private BigDecimal twoAgent;




}

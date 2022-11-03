package com.hjh.erp.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 会员对象 uc_user
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Data
public class UcUser {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long id;

    /**
     * $column.columnComment
     */
    private Date applicationTime;

    /**
     * 头像
     */
    @Excel(name = "头像")
    private String avatar;

    /**
     * 邀请人ID
     */
    @Excel(name = "邀请人ID")
    private Long inviterId;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 登录次数
     */
    @Excel(name = "登录次数")
    private Long loginCount;

    /**
     * $column.columnComment
     */
    private String mobilePhone;

    /**
     * $column.columnComment
     */
    private String password;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "注册时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date registrationTime;

    /**
     * $column.columnComment
     */
    private String salt;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private Long status;

    /**
     * 账号
     */
    @Excel(name = "账号")
    private String account;

    /**
     * $column.columnComment
     */
    private String superPartner;

    /**
     * TRC地址
     */
    @Excel(name = "TRC地址")
    private String trcAddress;

    /**
     * 用户名
     */
    @Excel(name = "用户名")
    private String username;

    /**
     * 登录ip
     */
    @Excel(name = "登录ip")
    private String ip;

    /**
     * $column.columnComment
     */
    private Long transactionStatus;

    /**
     * $column.columnComment
     */
    private String promotionCode;


}

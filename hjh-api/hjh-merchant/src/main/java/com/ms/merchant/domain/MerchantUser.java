package com.ms.merchant.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 用户对象 sys_user
 *
 * @author hjh
 */
@Data
public class MerchantUser {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long userId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;


    @TableField(exist = false)
    private List<MerchantRole> bindingMerchantRoleList;
    @TableField(exist = false)
    private List<MerchantRole> notBindingMerchantRoleList;

    @TableField(exist = false)
    private List<Long> roleIds;

    @TableField(exist = false)
    private String googleSign;

    private String googleSignCode;
    private Date lastLoginDate;

}

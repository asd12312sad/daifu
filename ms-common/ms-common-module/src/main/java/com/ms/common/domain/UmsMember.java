package com.ms.common.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UmsMember implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberLevelId;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private Integer status;

    private Date createTime;

    private String icon;

    private Byte gender;

    private Date birthday;

    private String city;

    private String job;

    private String personalizedSignature;

    private Integer sourceType;

    private Integer integration;

    private Integer growth;

    private Integer luckeyCount;

    private Integer historyIntegration;


    /**  3 用户类型;1:admin;2:会员 */
    private Byte userType;

    /**  30 身份证号1 */
    private String idCardNumber;

    /**  100 token值 */
    private String token;

    /**  10 级别id */
    private Integer level;

    private Long zsbId;


}
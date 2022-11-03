package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data

public class UcUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    @JsonIgnore
    private String salt;

    private String username;

    /**
     * 登录密码
     */
    private String password;

    private String account;
    private Integer status;
    private Integer inviterId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;


    //超级合伙人 标识0  普通 1超级合伙人 ...
    private String superPartner="0";


    /**
     * 登录次数
     */
    private Integer loginCount = 0;


    /**
     * 头像
     */
    private String avatar;

    private String ip;



    /**
     * trc20
     */
    private String trcAddress;
}

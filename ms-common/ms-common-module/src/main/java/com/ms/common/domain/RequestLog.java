package com.ms.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

//
@Data
public class RequestLog {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Long merchantId;

    private String requestLog;

    private String responseLog;

    private Integer type;

    private Long merchantUserId;


    private Integer status;

    private String merchantUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date requestTime;

    private String ip;



}

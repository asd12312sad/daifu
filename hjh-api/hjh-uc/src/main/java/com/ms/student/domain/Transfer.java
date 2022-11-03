package com.ms.student.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Transfer {

    @TableId(value = "tr_id", type = IdType.INPUT)
    private String trId;

    private Integer status;

    private Date createTime;

    private String msg;
}

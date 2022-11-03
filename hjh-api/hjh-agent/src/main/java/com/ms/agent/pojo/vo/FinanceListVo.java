package com.ms.agent.pojo.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FinanceListVo {


    @Excel(name = "商户名称")
    private String merchantName;

    @Excel(name = "收款类型")
    private String coinType="TRC20-USDT";

    @Excel(name = "业务类型")
    private String serviceName;

    @Excel(name = "交易金额")
    private BigDecimal amount;

    @Excel(name = "代理手续费")
    private BigDecimal agentAmount;

    @Excel(name = "商户手续费")
    private BigDecimal merchantAmount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer agentId;

    private Long merchantId;

    private Integer pageSize;

    private Integer pageNumber;

    private String sortColumn;

    private String sort;

    @TableField(exist = false)
    private String startDate;
    @TableField(exist = false)
    private String endDate;

}

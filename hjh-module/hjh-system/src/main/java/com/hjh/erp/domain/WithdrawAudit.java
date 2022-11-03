package com.hjh.erp.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hjh.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.hjh.common.core.domain.BaseEntity;

/**
 * 提币审核对象 withdraw_audit
 *
 * @author xiaobing
 * @date 2022-04-21
 */
@Data
public class WithdrawAudit  {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    /** 表ID */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 充币地址
     */
    @Excel(name = "充币地址")
    private String address;



    /**
     * 1充币 2 提币
     */
    private String tradeType;

    /**
     * 状态  3 成功 4 失败
     */
    @Excel(name = "状态  3 成功 4 失败")
    private Integer status;



    /**
     * 玩家ID
     */
    @Excel(name = "玩家ID")
    private Integer userId;

    /**
     * 提币数量
     */
    @Excel(name = "提币数量")
    private BigDecimal amount;

    /**
     * 玩家名称
     */
    @Excel(name = "玩家名称")
    private String userName;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * $column.columnComment
     */
    private Integer type;

    private String remark;


}

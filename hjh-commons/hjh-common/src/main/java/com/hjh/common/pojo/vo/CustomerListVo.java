package com.hjh.common.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hjh.common.annotation.Excel;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 客户对象 customer_list_dto
 *
 * @author xiaobing
 * @date 2021-08-23
 */
@Data
public class CustomerListVo {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名")
    private String name;

    /**
     * 客户性别
     */
    @Excel(name = "客户性别", readConverterExp = "0=男,1=女,2=未知")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sex;

    /**
     * 客户姓名，联系电话
     */
    private String searchValue;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String linkPhone;

    /**
     * 客户等级
     */
    @Excel(name = "客户等级", readConverterExp = "1=A,2=B,3=C,4=D,5=F")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long level;

    private List<Long> levelList;

    /**
     * 客户住址
     */
    @Excel(name = "客户住址")
    private String customerAddress;

    /**
     * 分配状态
     */
    @Excel(name = "分配状态", readConverterExp = "0=未分配,1=已分配")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long status;

    /**
     * 跟访状态
     */
    @Excel(name = "跟访状态", readConverterExp = "0=未跟访,1=已跟访")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitStatus;

    /**
     * 最新跟访时间
     */
    @Excel(name = "最新跟访时间", width = 15, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date laseVisitTime;

    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 15, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date assignTime;

    /**
     * 到店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date toShopTime;

    /**
     * 到店状态
     */
    @Excel(name = "到店状态")
    private Long toShopStatus;
    /**
     * 预约到店时间
     */
    @Excel(name = "预约到店时间")
    private Date appointmentTime;
    private String belongStaffName;

    private Map<String, Object> params;


    private String assignTimeStart;
    private String assignTimeEnd;
    private String laseVisitTimeStart;
    private String laseVisitTimeEnd;
}

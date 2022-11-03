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

/**
 * 跟访记录列对象 customer_visit_page
 *
 * @author xiaobing
 * @date 2021-08-24
 */
@Data
public class CustomerVisitPage {
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
    @Excel(name = "员工性别", readConverterExp = "0=男,1=女,2=未知")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sex;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String linkPhone;

    private List<String> customerSoureList;
    /**
     * 客户等级
     */
    @Excel(name = "客户等级")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long level;

    private List<Long> levelList;
    /**
     * 客户来源
     */
    @Excel(name = "客户来源")
    @JsonSerialize(using = ToStringSerializer.class)
    private String customerSoure;

    /**
     * 操作类型
     */
    @Excel(name = "操作类型")
    private String actionType;
    /**
     * 操作类型
     */
    private String[] actionTypeArray;
    /**
     * 分配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "分配时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;

    /**
     * 流程状态
     */
    @Excel(name = "流程状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processStatus;

    /**
     * 跟访状态
     */
    @Excel(name = "跟访状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitStatus;

    /**
     * 跟访时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "跟访时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime;


    /**
     * 所属员工名称
     */
    @Excel(name = "所属员工名称")
    private String belongStaffName;

    private Long timeAss;

    private String customerJson;

    private String searchValue;


    private String assignTimeStart;
    private String assignTimeEnd;
    private String visitTimeStart;
    private String visitTimeEnd;

}

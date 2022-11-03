package com.hjh.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hjh.common.annotation.Excel;
import com.hjh.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 客户对象 cms_customer
 *
 * @author xiaobing
 * @date 2021-08-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "cms_customer")
public class Customer extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 操作类型
     */
    @Excel(name = "操作类型", readConverterExp = "操作类型")
    private String actionType;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名", readConverterExp = "客户姓名")
    private String name;

    /**
     * 客户等级
     */
    @Excel(name = "客户等级", readConverterExp = "客户等级")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long level;
    /**
     * 渠道名称
     */
    @Excel(name = "渠道名称", readConverterExp = "渠道名称")
    private String channelName;
    /**
     * 联系电话
     */
    @Excel(name = "联系电话", readConverterExp = "联系电话")
    private String linkPhone;

    /**
     * 市场编码
     */
    @Excel(name = "市场编码", readConverterExp = "市场编码")
    private String cityCode;
    /**
     * 市场
     */
    @Excel(name = "市场", readConverterExp = "市场")
    private String cityName;

    private String leadsId;
    /**
     * 客户类型
     */
    @Excel(name = "客户类型", readConverterExp = "客户类型")
    private String type;

    /**
     * 跟访状态
     */
    @Excel(name = "跟访状态", readConverterExp = "跟访状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long visitStatus;

    /**
     * 客户来源
     */
    @Excel(name = "客户来源", readConverterExp = "客户来源")
    @JsonSerialize(using = ToStringSerializer.class)
    private String customerSoure;

    /**
     * 房屋类型
     */
    @Excel(name = "房屋类型", readConverterExp = "房屋类型")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long houseType;
    /**
     * 邀约状态
     */
    @Excel(name = "邀约状态", readConverterExp = "邀约状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer invitationStatus;

    /**
     * 客户住址
     */
    @Excel(name = "客户住址", readConverterExp = "客户住址")
    private String customerAddress;
    /**
     * 邀约/推荐人员
     */
    @Excel(name = "邀约/推荐人员", readConverterExp = "邀约/推荐人员")
    private String inviterUserName;

    /**
     * 到店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date toShopTime;

    /**
     * 离店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date outShopTime;
    /**
     * 客户性别
     */
    @Excel(name = "客户性别", readConverterExp = "客户性别")
    private Integer sex;

    /**
     * 户型
     */
    @Excel(name = "户型", readConverterExp = "户型")
    private String houseModel;

    /**
     * 建筑面积
     */
    @Excel(name = "建筑面积", readConverterExp = "建筑面积")
    private String coveredArea;

    /**
     * 居住楼层
     */
    @Excel(name = "居住楼层", readConverterExp = "居住楼层")
    private String residentialFloor;

    /**
     * 套内面积
     */
    @Excel(name = "套内面积", readConverterExp = "套内面积")
    private String insideSpace;

    /**
     * 有无电梯
     */
    @Excel(name = "有无电梯", readConverterExp = "有无电梯")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long haveElevator;

    /**
     * 所在小区
     */
    @Excel(name = "所在小区", readConverterExp = "所在小区")
    private String communityName;

    /**
     * 最新跟访时间
     */
    @Excel(name = "最新跟访时间", readConverterExp = "最新跟访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date laseVisitTime;

    /**
     * 最新跟访人
     */
    @Excel(name = "最新跟访人", readConverterExp = "最新跟访人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long laseVisitUserId;

    /**
     * 最新跟访人姓名
     */
    @Excel(name = "最新跟访人姓名", readConverterExp = "最新跟访人姓名")
    private String laseVisitUserName;

    /**
     * 关联类型
     */
    @Excel(name = "关联类型", readConverterExp = "关联类型")
    private String relevanceType;
    /**
     * 推荐人
     */
    private String referees;

    /**
     * 关联ID
     */
    @Excel(name = "关联ID", readConverterExp = "关联ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relevanceId;


    /**
     * 交房时间
     */
    private Date makingRoomTime;

    /**
     * 关联地址
     */
    @Excel(name = "关联地址", readConverterExp = "关联地址")
    private String relevanceUrl;

    /**
     * 邀约码
     */
    @Excel(name = "邀约码", readConverterExp = "邀约码")
    private String invitationCode;
    /**
     * 预约到店时间
     */
    @Excel(name = "预约到店时间")
    private Date appointmentTime;

    /**
     * 所属员工ID
     */
    @Excel(name = "所属员工ID", readConverterExp = "所属员工ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long belongStaffId;


    private Integer customerType;

    /**
     * 所属员工名称
     */
    @Excel(name = "所属员工名称", readConverterExp = "所属员工名称")
    private String belongStaffName;

    /**
     * 状态
     */
    @Excel(name = "状态", readConverterExp = "状态")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long status;
    /**
     * 分配时间
     */
    @Excel(name = "分配时间", readConverterExp = "分配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date assignTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerAssignId;


    /**
     * 装修风格
     */
    @Excel(name = "装修风格")
    private String decorateStyle;

    /**
     * 客户意向
     */
    @Excel(name = "客户意向")
    private Long customerIntention;

    /**
     * 回执单
     */
    @Excel(name = "回执单")
    private String singleReceipt;

    /**
     * 客服备注
     */
    @Excel(name = "客服备注")
    private String customerServiceRemark;

    /**
     * 到店状态
     */
    @Excel(name = "到店状态")
    private Long toShopStatus;

    /**
     * 所属客服名称
     */
    @Excel(name = "所属客服名称")
    private String belongStaffCustomerServiceName;

    /**
     * 所属客服id
     */
    @Excel(name = "所属客服id")
    private Long belongStaffCustomerServiceId;

    /**
     * 所属签单部员工名称
     */
    @Excel(name = "所属签单部员工名称")
    private String signEmployeesName;
    /**
     * 分配给签单部员工时间
     */
    @Excel(name = "分配给签单部员工时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date signEmployeesTime;
    /**
     * 所属签单部员工id
     */
    @Excel(name = "所属签单部员工id")
    private Long signEmployeesId;

    /**
     * 年龄
     */
    private String age;

    /**
     * 职业
     */
    @Excel(name = "职业")
    private String professional;

    /**
     * 职业 所在商圈
     */
    @Excel(name = "所在商圈")
    private Long businessCircle;

    private Integer customerIntentionStatus;


}

package com.ms.merchant.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 角色表 sys_role
 *
 * @author hjh
 */
@Data
public class MerchantRole{

    /**
     * 角色ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long roleId;

    private Long merchantId;


    @TableField(exist = false)
    private String googleSign;

    /**
     * 角色名称
     */
    private String roleName;



    /**
     * 绑定菜单组
     */
    @TableField(exist = false)
    private List<MerchantMenu> bindingMerchantMenuList;

    /**
     * 未绑定菜单组
     */
    @TableField(exist = false)
    private List<MerchantMenu> notBindingMerchantMenuList;

    @TableField(exist = false)
    private List<Long> menuIds;

}

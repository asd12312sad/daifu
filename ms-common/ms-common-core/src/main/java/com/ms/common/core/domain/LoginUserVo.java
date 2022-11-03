package com.ms.common.core.domain;

import lombok.Data;

/**
 * Redis中存储的用户信息
 *
 * @author xiaobing
 */
@Data
public class LoginUserVo {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 登录用户（请求头传递时需要编码）
     */
    private String name;

    /**
     * 密码（不存储到redis）
     */
    private String password;

    /**
     * 登录类型（商户/用户）
     */
    private String loginType;

    /**
     * TOKEN
     */
    private String token;
    /**
     * 登录时间（配合TOKEN进行重复登录验证）
     */
    private Long loginTime;


    /**
     * 用户登录ID
     */
    private Integer parentId;

    /**
     * 用户登录名称
     */
    private String parentName;

    /**
     * token过期时间
     */
    private Long expireTime;


}

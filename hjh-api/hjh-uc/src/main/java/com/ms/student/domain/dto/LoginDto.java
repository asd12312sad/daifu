package com.ms.student.domain.dto;

import lombok.Data;

/**
 * LoginDto -- 登录类
 *
 * @author XiaoBing
 * @version v1.0.0
 * @date 2020/12/8
 * @Description Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2020/12/8             XiaoBing          v1.0.0           Created
 */
@Data
public class LoginDto {
    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;


    /**
     * 验证码
     */
    private String code;

    private String registrationId;
}

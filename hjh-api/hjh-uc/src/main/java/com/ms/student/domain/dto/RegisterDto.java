package com.ms.student.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegisterDto {

    /**
     * 账号
     */
    @NotNull(message = "账号不能为空")
    private String account;
    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;


    /**
     * trc20
     */
    @NotNull(message = "trc20不能为空")
    private String trcAddress;

    private String inviteCode;
}

package com.ms.common.domain;

import lombok.Data;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/1/1
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/1/1              肖兵             v1.0.0           Created
 */
@Data
public class UpdatePassWordDto {

    private String password;

    private String account;

    private String code;
}

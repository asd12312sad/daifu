package com.ms.student.domain.vo;

import lombok.Data;

/**
 * LoginVo -- 登录返回Vo
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
public class LoginVo {


    private String tokenType;

    private String token;


}

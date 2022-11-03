package com.ms.common.core.annotation;


import com.ms.common.core.enums.UserType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * C端用户访问鉴权 （防止恶意请求其他平台接口）
 *
 * @author xiaobing
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenTypeAnnotation {

    UserType[] tokenTypeCan();
}

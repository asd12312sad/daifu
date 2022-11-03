package com.ms.common.core.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用户重复点击验证（提交时间）
 * 防止恶意提交导致数据重复的问题
 *
 * @author xiaobing
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SameUrlData {

}

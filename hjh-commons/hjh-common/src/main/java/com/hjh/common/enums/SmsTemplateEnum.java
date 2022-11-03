package com.hjh.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信模板
 *
 * @author xiao bing
 */
@Getter
@AllArgsConstructor
public enum SmsTemplateEnum {

    /**
     * 登录
     */
    LOG_IN(0, "SMS_224655022"),

    /**
     * 注册
     */
    REGISTERED(1, "SMS_224655022"),

    /**
     * 忘记密码
     */
    FORGET_PASSWORD(2, "SMS_224655022"),


    /**
     * 数据变更
     */
    UPDATE_DATA(3, "SMS_224655022");


    private final int type;

    private final String template;


    public static String getTemplateByType(int type) {

        SmsTemplateEnum[] values = SmsTemplateEnum.values();

        for (SmsTemplateEnum value : values) {
            if (value.type == type) {
                return value.template;
            }
        }

        return null;
    }


}

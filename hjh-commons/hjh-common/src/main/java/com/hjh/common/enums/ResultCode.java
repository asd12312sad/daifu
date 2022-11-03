package com.hjh.common.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author fcl
 * @version v1.0.0
 * @date 2019-10-23
 * @Description 全局返回状态码
 */
public enum ResultCode {

    /**
     * 请求成功
     */
    SUCCESS("200", "SUCCESS"),
    ISNULL("401", "暂无相关数据"),

    ERROR("1111", "ERROR"),

    CODE_TIME("1112", "验证码时间至少间隔一分钟"),
    CODE_ERROR("1113", "验证码错误"),
    REGISTRY_REPETITION("1114", "当前手机号已经被注册"),
    REGISTRY_NOTHAS("1115", "用户不存在"),
    PASSWORD_ERRO("1116", "用户名或密码错误"),
    ACCOUNT_REPETITION("1117", "登录账号重复"),
    NAME_REPETITION("1118", "用户名重复"),

    /**
     * 账号被禁用提示
     */
    ACCOUNT_HAS_BEEN_DISABLED("1113", "账号已被禁用"),

    /**
     * 请求接口鉴权时使用
     */
    TOKEN_TIME_OUT("2001", "TOKEN过期"),

    /**
     * 请求接口鉴权时使用
     */
    NO_AUTH_CODE("2002", "TOKEN异常"),

    /**
     * 请求接口鉴权时使用
     */
    INSUFFICIENT_PERMISSIONS("2003", "权限不足"),
    /**
     * 您的账号已在别的设备登陆
     */
    YOUR_ACCOUNT_HAS_BEEN_LOGGED_IN_ON_ANOTHER_DEVICE("2004", "您的账号已在别的设备登陆"),

    CFTJ("3992", "请勿重复点击"),

    BUSINESS_LIMIT_CONTROL("4000", "每个手机号每天只能发送10条"),
    SMS_ERROR("4001", "服务繁忙，请稍后再试"),
    PHONE_ERROR("4002", "请输入正确的手机号码！");


    private final Integer code;

    private final String msg;

    ResultCode(String code, String msg) {
        this.code = Integer.parseInt(code);
        this.msg = msg;
    }

    /**
     * 将该枚举全部转化成json
     *
     * @return
     */
    public static JSONArray toJson() {
        JSONArray jsonArray = new JSONArray();
        for (ResultCode e : ResultCode.values()) {
            JSONObject object = new JSONObject();
            object.put("code", e.getCode());
            object.put("msg", e.getMsg());
            jsonArray.add(object);
        }
        return jsonArray;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return msg;
    }

}

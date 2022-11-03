package com.ms.common.domain;

import java.io.Serializable;

import com.ms.common.core.constant.Constants;
import com.ms.common.core.domain.ResultCode;
import com.ms.common.utils.ServletUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 响应信息主体
 *
 * @author xiaobing
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = Constants.SUCCESS;

    /**
     * 失败
     */
    public static final int failed = Constants.FAIL;

    private int code;

    private String msg;

    private T data;

    public static <T> R<T> success() {
        return restResult(null, SUCCESS, "SUCCESS");
    }

    public static <T> R<T> success(T data) {

        return restResult(data, SUCCESS, "SUCCESS");
    }

    public static <T> R<T> success(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> R<T> success(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, failed, null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, failed, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, failed, null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, failed, msg);
    }

    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> R<T> failed(int code, String msg, T data) {
        return restResult(data, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {

        HttpServletRequest request = ServletUtils.getRequest();
        R<T> apiResult = new R<T>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        request.setAttribute("returnData",apiResult);

        return apiResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * @param resultCode
     */
    public static <T> R<T> setToCode(ResultCode resultCode) {
        return restResult(null, resultCode.getCode(), resultCode.getMsg());
    }
}

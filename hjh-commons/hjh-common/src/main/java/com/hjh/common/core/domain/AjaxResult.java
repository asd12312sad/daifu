package com.hjh.common.core.domain;

import java.util.List;

import com.hjh.common.constant.Constants;
import com.hjh.common.constant.HttpStatus;
import com.hjh.common.enums.ResultCode;
import com.hjh.common.utils.StringUtils;
import lombok.Data;

/**
 * 操作消息提醒
 *
 * @author hjh
 */
@Data
public class AjaxResult<T> {
    private static final long serialVersionUID = 1L;
    /**
     * 成功
     */
    public static final String SUCCESS = Constants.SUCCESS;

    /**
     * 失败
     */
    public static final String FAIL = Constants.FAIL;

    /**
     * 状态码
     */
    public int code;

    /**
     * 返回内容
     */
    public String msg;

    /**
     * 数据对象
     */
    public T data;

    public List<T> row;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult() {
    }


    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult<Object> toAjax(boolean flag) {
        if (flag)
            return success("操作成功");
        return error();
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success() {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(T data) {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(String msg) {
        return restResult(HttpStatus.SUCCESS, msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(String msg, T data) {
        return restResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return 错误
     */
    public static <T> AjaxResult<T> error() {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg) {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg * @param data 数据对象返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg, T data) {
        return restResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(int code, String msg) {
        return restResult(code, msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(int code, String msg, T data) {
        return restResult(code, msg, data);
    }

    private static <T> AjaxResult<T> restResult(Integer code, String msg, T data) {
        AjaxResult<T> apiResult = new AjaxResult<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public static AjaxResult setToCode(ResultCode resultCode) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult.setMsg(resultCode.getMsg());

        ajaxResult.setCode(resultCode.getCode());
        return ajaxResult;
    }
}

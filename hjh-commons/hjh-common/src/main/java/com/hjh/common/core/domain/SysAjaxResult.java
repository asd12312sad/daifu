package com.hjh.common.core.domain;

import java.util.HashMap;

import com.hjh.common.constant.HttpStatus;
import com.hjh.common.utils.StringUtils;

/**
 * 操作消息提醒
 *
 * @author hjh
 */
public class SysAjaxResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 SysAjaxResult 对象，使其表示一个空消息。
     */
    public SysAjaxResult() {
    }

    /**
     * 初始化一个新创建的 SysAjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public SysAjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 SysAjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public SysAjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data)) {
            super.put(DATA_TAG, data);
        }
    }


    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static SysAjaxResult toAjax(boolean flag) {
        if (flag)
            return success("操作成功");
        return error();
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static SysAjaxResult success() {
        return SysAjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static SysAjaxResult success(Object data) {
        return SysAjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static SysAjaxResult success(String msg) {
        return SysAjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static SysAjaxResult success(String msg, Object data) {
        return new SysAjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static SysAjaxResult error() {
        return SysAjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static SysAjaxResult error(String msg) {
        return SysAjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static SysAjaxResult error(String msg, Object data) {
        return new SysAjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static SysAjaxResult error(int code, String msg) {
        return new SysAjaxResult(code, msg, null);
    }
}

package com.ms.common.core.enums;

/**
 * 全局异常代码
 * 自定义异常必须使用该异常代码标注
 *
 * @author zhouqi
 * @version v1.0.0
 * @date 2019-06-02 18:19
 * @Description Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2019-06-02 18:19     zhouqi          v1.0.0           Created
 */
public enum GlobalExceptionCode {


    ///以下为示例，后期需要删除

    NOT_FOUND_EXCEPTION_CODE("3", "资源未找到"),

    METHOD_ARGUMENT_EXCEPTION_CODE("4", "方法参数异常"),

    FALLBACK_EXCEPTION_CODE("5", "服务降级"),

    DATA_PERSISTENCE_EXCEPTION_CODE("6", "数据持久化异常"),

    DATA_OVER_AND_OVER_AGAIN("7", "数据重复");


    /**
     * 异常代码
     */
    private String code;

    /**
     * 异常信息
     */
    private String msg;


    GlobalExceptionCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

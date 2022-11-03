package com.hjh.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/18 17:05
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/18 17:05        肖兵           v1.0.0           Created
 */
public class GsonUtil {
    /**
     * 缺省时间格式，开发人员可根据项目需要更改此常量
     */
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";


    /**
     * 将一个对象转换为JSON字符串，使用缺省时间格式
     *
     * @param object 要进行转换的对象
     * @return JSON字符串，时间格式为java.util.Date类的toString()的结果
     */
    public static String getJson(Object object) {
        return new Gson().toJson(object);
    }

    /**
     * 将一个对象转换成JSON字符串，并进行时间格式化
     *
     * @param object  要进行转换的对象
     * @param pattern 时间类型转换格式，若该参数为null或""时，使用缺省时间格式，实际调用时应当注意时间格式的有效性
     * @return
     */
    public static String getJson(Object object, String pattern) {

        if (StringUtils.isEmpty(pattern)) {
            return new GsonBuilder().setDateFormat(GsonUtil.PATTERN).create().toJson(object);
        }

        return new GsonBuilder().setDateFormat(pattern).create().toJson(object);
    }


    /**
     * 将一个JSON字符串转换成一个对象,需要注意json字符串中不能有时间格式属性（Gson对时间格式缺省情况下支持的不多），若有时间类型的属性，需要使用getObject(String json, Class c, String pattern)
     *
     * @param json 要转换的JSON字符串
     * @param c    转换类型
     * @return
     */
    public static Object getObject(String json, Class c) {
        return new Gson().fromJson(json, c);
    }

    /**
     * 将一个JSON字符串转换成一个对象，并按照对应的时间格式对时间类型的属性进行格式化
     *
     * @param json    要转换的JSON字符串
     * @param c       转换类型
     * @param pattern 时间类型转换格式，若该参数为null或""时，使用缺省时间格式，实际调用时应当注意时间格式的有效性
     * @return
     */
    public static Object getObject(String json, Class c, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return new GsonBuilder().setDateFormat(GsonUtil.PATTERN).create().fromJson(json, c);
        }

        return new GsonBuilder().setDateFormat(pattern).create().fromJson(json, c);
    }

}

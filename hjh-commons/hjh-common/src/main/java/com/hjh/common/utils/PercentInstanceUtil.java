package com.hjh.common.utils;

import java.text.NumberFormat;

/**
 * 百分比工具
 *
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/26 15:46
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/26 15:46        肖兵           v1.0.0           Created
 */

public class PercentInstanceUtil {

    /**
     * 百分比
     *
     * @param detailTotalNumber 数
     * @param totalNumber       总数
     *                          占比=数/总数
     * @return 百分比
     */
    public static String percentInstance(Integer totalNumber, Integer detailTotalNumber) {
        if (totalNumber == 0) {
            return "100%";
        }
        if (detailTotalNumber == 0) {
            return "0%";
        }
        Double bfTotalNumber = Double.valueOf(detailTotalNumber);
        Double zcTotalNumber = Double.valueOf(totalNumber);
        double percent = bfTotalNumber / zcTotalNumber;
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        return nt.format(percent) + "%";
    }

}

package com.ms.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/1/1
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/1/1              肖兵             v1.0.0           Created
 */
@Getter
@AllArgsConstructor
public enum  EquipmentType {
    /**
     * 登录
     */
    KQ(0, "考勤机"),

    /**
     * 注册
     */
    SX(1, "监控头");




    private final int type;

    private final String template;


    public static String getEquipmentType(int type) {

        EquipmentType[] values = EquipmentType.values();

        for (EquipmentType value : values) {
            if (value.type == type) {
                return value.template;
            }
        }

        return null;
    }

}

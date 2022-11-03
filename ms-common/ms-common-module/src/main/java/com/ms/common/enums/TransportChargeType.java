/*
 * Copyright (c) 2018-2999 广州亚米信息科技有限公司 All rights reserved.
 *
 * https://www.gz-yami.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.ms.common.enums;


/**
 * 运费收费方式 （0 按件数,1 按重量 2 按体积）
 * @author LGH
 */
public enum TransportChargeType {


    /**
     * 按件数
     */
    COUNT(0),

    /**
     * 按重量
     */
    WEIGHT(1),

    /**
     * 按体积
     */
    VOLUME(2)
    ;

    private Integer num;

    public Integer value() {
        return num;
    }

    TransportChargeType(Integer num){
        this.num = num;
    }

    public static TransportChargeType instance(Integer value) {
        TransportChargeType[] enums = values();
        for (TransportChargeType statusEnum : enums) {
            if (statusEnum.value().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}

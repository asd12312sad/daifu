package com.hjh.common.pojo.vo;

import lombok.Data;

/**
 * 用户统计数据
 *
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/26 16:45
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/26 16:45        肖兵           v1.0.0           Created
 */
@Data
public class UserStatisticsHeaderVo {
    /**
     * 邀约数量
     */
    private Integer inviteCount;
    /**
     * 邀约数量
     */
    private Integer inviteAllCount;


    /**
     * 有效数量
     */
    private Integer effectiveCount;

    /**
     * 有效数量
     */
    private Integer effectiveAllCount;
    /**
     * 到店数量
     */
    private Integer toShopCount;
    /**
     * 到店数量
     */
    private Integer toShopAllCount;

    /**
     * 邀约数量
     */
    private Integer allCount;

    private String effectiveProportion;
    private String toShopProportion;
    private String inviteProportion;


}

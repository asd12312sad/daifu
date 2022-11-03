package com.hjh.common.pojo.vo;

import com.hjh.common.annotation.Excel;
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
public class UserStatisticsPageVo {
    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 来源ID
     */
    @Excel(name = "来源")
    private String sourceName;
    /**
     * 分配数量
     */
    @Excel(name = "分配数量")
    private Integer allCount;

    /**
     * 有效数量
     */
    @Excel(name = "有效数量")
    private Integer effectiveCount;
    /**
     * 邀约数量
     */
    @Excel(name = "邀约数量")
    private Integer inviteCount;

    /**
     * 到店数量
     */
    @Excel(name = "到店数量")
    private Integer toShopCount;
    /**
     * 签单数量
     */
    @Excel(name = "签单数量")
    private Integer signCount;


    /**
     * Can't connect to local MySQL server through socket '/var/lib/mysql/mysql.sock' (2)
     * 有效率
     */
    @Excel(name = "有效率")
    private String effectiveProportion;
    /**
     * 邀约率
     */
    @Excel(name = "邀约率")
    private String inviteProportion;

    /**
     * 到店率
     */
    @Excel(name = "到店率")
    private String toShopProportion;
    /**
     * 签单率
     */
    @Excel(name = "签单率")
    private String signCountProportion;

    /**
     * 到店占比
     */
    @Excel(name = "到店占比")
    private String toShopPercentage;

}

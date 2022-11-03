package com.ms.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/1/6
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/1/6              肖兵             v1.0.0           Created
 */
@Data
public class TbVersion {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String version;


    private Integer deviceSide;

    private String url;

    /**
     * 版本名称
     */
    private String versionName;
}

package com.hjh.erp.service;

import com.hjh.erp.domain.SystemInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 系统日志Service接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface ISystemInfoService  extends IService<SystemInfo>
{

    /**
     * 查询系统日志列表
     *
     * @param systemInfo 系统日志
     * @return 系统日志集合
     */
    public List<SystemInfo> selectSystemInfoList(SystemInfo systemInfo);

}

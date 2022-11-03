package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.SystemInfoMapper;
import com.hjh.erp.domain.SystemInfo;
import com.hjh.erp.service.ISystemInfoService;

/**
 * 系统日志Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Service
public class SystemInfoServiceImpl extends ServiceImpl<SystemInfoMapper,SystemInfo> implements ISystemInfoService
{



    /**
     * 查询系统日志列表
     *
     * @param systemInfo 系统日志
     * @return 系统日志集合
     */
    @Override
    public List<SystemInfo> selectSystemInfoList(SystemInfo systemInfo){

       return  this.baseMapper.selectSystemInfoList(systemInfo,new HashMap<>());

    }
}

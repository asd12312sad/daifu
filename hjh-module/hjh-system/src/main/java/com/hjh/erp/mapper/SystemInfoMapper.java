package com.hjh.erp.mapper;

import com.hjh.erp.domain.SystemInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统日志Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface SystemInfoMapper extends BaseMapper<SystemInfo>
{

/**
 * 查询系统日志列表
 *
 * @param systemInfo 系统日志
 * @return 系统日志集合
 */
public List<SystemInfo> selectSystemInfoList(@Param("systemInfo")SystemInfo systemInfo, @Param("params") Map<String, Object> params);


    /**
     * 查询系统日志
     *
     * @param id 系统日志主键
     * @return 系统日志
     */
    public SystemInfo selectSystemInfoById(Long id);

}

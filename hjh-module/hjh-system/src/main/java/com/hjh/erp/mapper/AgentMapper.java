package com.hjh.erp.mapper;

import com.hjh.erp.domain.Agent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 代理Mapper接口
 *
 * @author xiaobing
 * @date 2022-07-09 22:39:14
 */
public interface AgentMapper extends BaseMapper<Agent>
{

/**
 * 查询代理列表
 *
 * @param agent 代理
 * @return 代理集合
 */
public List<Agent> selectAgentList(@Param("agent")Agent agent, @Param("params") Map<String, Object> params);


    /**
     * 查询代理
     *
     * @param id 代理主键
     * @return 代理
     */
    public Agent selectAgentById(Long id);

}

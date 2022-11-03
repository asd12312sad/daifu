package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.AgentMapper;
import com.hjh.erp.domain.Agent;
import com.hjh.erp.service.IAgentService;

/**
 * 代理Service业务层处理
 *
 * @author xiaobing
 * @date 2022-07-09 22:39:14
 */
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper,Agent> implements IAgentService
{



    /**
     * 查询代理列表
     *
     * @param agent 代理
     * @return 代理集合
     */
    @Override
    public List<Agent> selectAgentList(Agent agent){
        if (agent.getParams() == null){
            agent.setParams(new HashMap<>());
        }
       return  this.baseMapper.selectAgentList(agent,agent.getParams());

    }
}

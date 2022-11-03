package com.ms.agent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.agent.domain.Agent;
import com.ms.agent.mapper.AgentMapper;
import com.ms.agent.service.AgentService;
import org.springframework.stereotype.Service;

@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService {

}

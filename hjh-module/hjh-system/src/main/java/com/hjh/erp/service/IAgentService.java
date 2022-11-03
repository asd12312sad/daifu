package com.hjh.erp.service;

import com.hjh.erp.domain.Agent;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 代理Service接口
 *
 * @author xiaobing
 * @date 2022-07-09 22:39:14
 */
public interface IAgentService  extends IService<Agent>
{

    /**
     * 查询代理列表
     *
     * @param agent 代理
     * @return 代理集合
     */
    public List<Agent> selectAgentList(Agent agent);

}

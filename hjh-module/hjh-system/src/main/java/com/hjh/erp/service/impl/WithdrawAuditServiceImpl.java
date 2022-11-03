package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjh.erp.domain.Order;
import com.hjh.erp.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.WithdrawAuditMapper;
import com.hjh.erp.domain.WithdrawAudit;
import com.hjh.erp.service.IWithdrawAuditService;

/**
 * 提币审核Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-21
 */
@Service
public class WithdrawAuditServiceImpl extends ServiceImpl<WithdrawAuditMapper,WithdrawAudit> implements IWithdrawAuditService
{



    @Autowired
    private IOrderService orderService;
    /**
     * 查询提币审核列表
     *
     * @param withdrawAudit 提币审核
     * @return 提币审核集合
     */
    @Override
    public List<WithdrawAudit> selectWithdrawAuditList(WithdrawAudit withdrawAudit){
       return  this.baseMapper.selectWithdrawAuditList(withdrawAudit,new HashMap<>());

    }

    @Override
    public boolean audit(WithdrawAudit withdrawAuditAudit) {
        WithdrawAudit withdrawAudit = getById(withdrawAuditAudit.getId());
        withdrawAudit.setStatus(withdrawAuditAudit.getStatus());

        if (withdrawAudit.getType() == 4) {    //反水
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", withdrawAudit.getUserId());
            queryWrapper.eq("trade_type", 1);
            queryWrapper.eq("status", 3);
            queryWrapper.eq("fanshui", 2);
            List<Order> orderList = orderService.list(queryWrapper);
            for (Order order1 : orderList) {
                order1.setFanshui(3);
                orderService.updateById(order1);
            }
        }else
        if (withdrawAudit.getType() == 5) {    //反水
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("proxy_one_id", withdrawAudit.getUserId());
            queryWrapper.eq("trade_type", 1);
            queryWrapper.eq("status", 3);
            queryWrapper.eq("tuiguang_one", 2);
            List<Order> orderList = orderService.list(queryWrapper);
            for (Order order1 : orderList) {
                order1.setTuiguangOne(3);
                orderService.updateById(order1);
            }
            queryWrapper.eq("proxy_one_id", withdrawAudit.getUserId());
            queryWrapper.eq("trade_type", 1);
            queryWrapper.eq("status", 3);
            queryWrapper.eq("tuiguang_two", 2);
            orderList = orderService.list(queryWrapper);
            for (Order order1 : orderList) {
                order1.setTuiguangTwo(3);
                orderService.updateById(order1);
            }
        }
        return updateById(withdrawAudit);
    }
}

package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import com.hjh.erp.mapper.UcUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.OrderMapper;
import com.hjh.erp.domain.Order;
import com.hjh.erp.service.IOrderService;

/**
 * 下注记录Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-15
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements IOrderService
{



    /**
     * 查询下注记录列表
     *
     * @param order 下注记录
     * @return 下注记录集合
     */
    @Override
    public List<Order> selectOrderList(Order order){
        if (order.getParams() == null){
            order.setParams(new HashMap<>());
        }
       return  this.baseMapper.selectOrderList(order,order.getParams());

    }
}

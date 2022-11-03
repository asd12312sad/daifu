package com.hjh.erp.service;

import com.hjh.erp.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 下注记录Service接口
 *
 * @author xiaobing
 * @date 2022-04-15
 */
public interface IOrderService  extends IService<Order>
{

    /**
     * 查询下注记录列表
     *
     * @param order 下注记录
     * @return 下注记录集合
     */
    public List<Order> selectOrderList(Order order);

}

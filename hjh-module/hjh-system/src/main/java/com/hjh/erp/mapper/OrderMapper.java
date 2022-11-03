package com.hjh.erp.mapper;

import com.hjh.erp.domain.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 下注记录Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-15
 */
public interface OrderMapper extends BaseMapper<Order>
{

/**
 * 查询下注记录列表
 *
 * @param order 下注记录
 * @return 下注记录集合
 */
public List<Order> selectOrderList(@Param("order")Order order, @Param("params") Map<String, Object> params);


    /**
     * 查询下注记录
     *
     * @param id 下注记录主键
     * @return 下注记录
     */
    public Order selectOrderById(Long id);

    /**
     * 查询游戏统计
     * @return 查询游戏统计
     */
    List<Map> selectSum();


}

package com.hjh.erp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.poi.ExcelUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hjh.common.annotation.Log;
import com.hjh.common.core.controller.BaseController;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.enums.BusinessType;
import com.hjh.erp.domain.Order;
import com.hjh.erp.service.IOrderService;

/**
 * 下注记录Controller
 *
 * @author xiaobing
 * @date 2022-04-15
 */
@RestController
@RequestMapping("/erp/order")
public class OrderController extends BaseController {
    @Autowired
    private IOrderService orderService;

    /**
     * 查询下注记录列表
     */
    @PreAuthorize("@ss.hasPermi('erp:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(Order order) {
        startPage();
        order.setTradeType("1");
        List<Order> page = orderService.selectOrderList(order);
        return getDataTable(page);
    }

    /**
     * 查询下注记录列表
     */
    @PreAuthorize("@ss.hasPermi('erp:order:list')")
    @GetMapping("/returnMoney")
    public TableDataInfo returnMoney(Order order) {
        startPage();
        order.setTradeType("2");
        List<Order> page = orderService.selectOrderList(order);
        return getDataTable(page);
    }

    /**
     * 导出下注记录列表
     */
    @PreAuthorize("@ss.hasPermi('erp:order:export')")
    @Log(title = "下注记录" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Order order) {
        List<Order> list = orderService.selectOrderList(order);
        ExcelUtil<Order> util = new ExcelUtil<Order>(Order. class);
        return util.exportExcel(list, "下注记录数据");
    }

    /**
     * 获取下注记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(orderService.getById(id));
    }

    /**
     * 新增下注记录
     */
    @PreAuthorize("@ss.hasPermi('erp:order:add')")
    @Log(title = "下注记录" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Order order) {
        return toAjax(orderService.save(order));
    }

    /**
     * 修改下注记录
     */
    @PreAuthorize("@ss.hasPermi('erp:order:edit')")
    @Log(title = "下注记录" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Order order) {
        return AjaxResult.toAjax(orderService.updateById(order));
    }

    /**
     * 删除下注记录
     */
    @PreAuthorize("@ss.hasPermi('erp:order:remove')")
    @Log(title = "下注记录" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(orderService.removeByIds(idList));
    }
}

package com.hjh.erp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.http.HttpUtil;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.poi.ExcelUtil;
import com.hjh.erp.domain.MerchantPayAddress;
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
import com.hjh.erp.domain.MerchantPayOrder;
import com.hjh.erp.service.IMerchantPayOrderService;

/**
 * 商户代付记录Controller
 *
 * @author xiaobing
 * @date 2022-06-10
 */
@RestController
@RequestMapping("/erp/payOrder")
public class MerchantPayOrderController extends BaseController {
    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;

    /**
     * 查询商户代付记录列表
     */
    @PreAuthorize("@ss.hasPermi('erp:payOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(MerchantPayOrder merchantPayOrder) {
        startPage();
        List<MerchantPayOrder> page = merchantPayOrderService.selectMerchantPayOrderList(merchantPayOrder);
        return getDataTable(page);
    }

    /**
     * 导出商户代付记录列表
     */
    @PreAuthorize("@ss.hasPermi('erp:payOrder:export')")
    @Log(title = "商户代付记录" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(MerchantPayOrder merchantPayOrder) {
        List<MerchantPayOrder> list = merchantPayOrderService.selectMerchantPayOrderList(merchantPayOrder);
        ExcelUtil<MerchantPayOrder> util = new ExcelUtil<MerchantPayOrder>(MerchantPayOrder. class);
        return util.exportExcel(list, "商户代付记录数据");
    }

    /**
     * 获取商户代付记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:payOrder:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(merchantPayOrderService.getById(id));
    }

    /**
     * 查询商户付款地址列表
     */
//    @PreAuthorize("@ss.hasPermi('system:address:list')")
//    @GetMapping("/huidiao")
    public AjaxResult huidiao(String  id,String adminGoogleSign) {

        MerchantPayOrder address = merchantPayOrderService.getById(id);
        String returnMsg = HttpUtil.get(address.getReturnMsg());
        if(returnMsg!= null && returnMsg.toUpperCase().equals("SUCCESS")){
            address.setMessage("SUCCESS");
        }else {
            address.setMessage(returnMsg);
        }
        return toAjax(merchantPayOrderService.updateById(address));
    }
    /**
     * 新增商户代付记录
     */
//    @PreAuthorize("@ss.hasPermi('erp:payOrder:add')")
//    @Log(title = "商户代付记录" , businessType = BusinessType.INSERT)
//    @PostMapping
    public AjaxResult add(@RequestBody MerchantPayOrder merchantPayOrder) {
        return toAjax(merchantPayOrderService.save(merchantPayOrder));
    }

    /**
     * 修改商户代付记录
     */
//    @PreAuthorize("@ss.hasPermi('erp:payOrder:edit')")
//    @Log(title = "商户代付记录" , businessType = BusinessType.UPDATE)
//    @PutMapping
    public AjaxResult edit(@RequestBody MerchantPayOrder merchantPayOrder) {
        return AjaxResult.toAjax(merchantPayOrderService.updateById(merchantPayOrder));
    }

    /**
     * 删除商户代付记录
     */
//    @PreAuthorize("@ss.hasPermi('erp:payOrder:remove')")
//    @Log(title = "商户代付记录" , businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(merchantPayOrderService.removeByIds(idList));
    }
}

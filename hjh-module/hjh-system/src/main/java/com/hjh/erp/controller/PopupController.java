package com.hjh.erp.controller;

import com.hjh.common.annotation.Log;
import com.hjh.common.core.controller.BaseController;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.enums.BusinessType;
import com.hjh.common.utils.poi.ExcelUtil;
import com.hjh.erp.domain.Popup;
import com.hjh.erp.service.IPopupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 弹窗Controller
 *
 * @author xiaobing
 * @date 2022-04-22 06:14:52
 */
@RestController
@RequestMapping("/erp/popup")
public class PopupController extends BaseController {
    @Autowired
    private IPopupService popupService;

    /**
     * 查询弹窗列表
     */
    @PreAuthorize("@ss.hasPermi('erp:popup:list')")
    @GetMapping("/list")
    public TableDataInfo list(Popup popup) {
        startPage();
        List<Popup> page = popupService.selectPopupList(popup);
        return getDataTable(page);
    }

    /**
     * 导出弹窗列表
     */
    @PreAuthorize("@ss.hasPermi('erp:popup:export')")
    @Log(title = "弹窗" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Popup popup) {
        List<Popup> list = popupService.selectPopupList(popup);
        ExcelUtil<Popup> util = new ExcelUtil<Popup>(Popup. class);
        return util.exportExcel(list, "弹窗数据");
    }

    /**
     * 获取弹窗详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:popup:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(popupService.getById(id));
    }

    /**
     * 新增弹窗
     */
    @PreAuthorize("@ss.hasPermi('erp:popup:add')")
    @Log(title = "弹窗" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Popup popup) {
        return toAjax(popupService.save(popup));
    }

    /**
     * 修改弹窗
     */
    @PreAuthorize("@ss.hasPermi('erp:popup:edit')")
    @Log(title = "弹窗" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Popup popup) {
        return AjaxResult.toAjax(popupService.updateById(popup));
    }

    /**
     * 删除弹窗
     */
    @PreAuthorize("@ss.hasPermi('erp:popup:remove')")
    @Log(title = "弹窗" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(popupService.removeByIds(idList));
    }
}

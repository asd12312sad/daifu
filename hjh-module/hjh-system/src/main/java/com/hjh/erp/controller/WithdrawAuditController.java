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
import com.hjh.erp.domain.WithdrawAudit;
import com.hjh.erp.service.IWithdrawAuditService;

/**
 * 提币审核Controller
 *
 * @author xiaobing
 * @date 2022-04-21
 */
@RestController
@RequestMapping("/erp/audit")
public class WithdrawAuditController extends BaseController {
    @Autowired
    private IWithdrawAuditService withdrawAuditService;

    /**
     * 查询提币审核列表
     */
    @PreAuthorize("@ss.hasPermi('erp:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(WithdrawAudit withdrawAudit) {
        startPage();
        List<WithdrawAudit> page = withdrawAuditService.selectWithdrawAuditList(withdrawAudit);
        return getDataTable(page);
    }

    /**
     * 导出提币审核列表
     */
    @PreAuthorize("@ss.hasPermi('erp:audit:export')")
    @Log(title = "提币审核" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(WithdrawAudit withdrawAudit) {
        List<WithdrawAudit> list = withdrawAuditService.selectWithdrawAuditList(withdrawAudit);
        ExcelUtil<WithdrawAudit> util = new ExcelUtil<WithdrawAudit>(WithdrawAudit. class);
        return util.exportExcel(list, "提币审核数据");
    }

    /**
     * 获取提币审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:audit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(withdrawAuditService.getById(id));
    }

    /**
     * 新增提币审核
     */
    @PreAuthorize("@ss.hasPermi('erp:audit:add')")
    @Log(title = "提币审核" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult audit(@RequestBody WithdrawAudit withdrawAudit) {
        return toAjax(withdrawAuditService.audit(withdrawAudit));
    }

    /**
     * 修改提币审核
     */
    @PreAuthorize("@ss.hasPermi('erp:audit:edit')")
    @Log(title = "提币审核" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WithdrawAudit withdrawAudit) {
        return AjaxResult.toAjax(withdrawAuditService.updateById(withdrawAudit));
    }

    /**
     * 删除提币审核
     */
    @PreAuthorize("@ss.hasPermi('erp:audit:remove')")
    @Log(title = "提币审核" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(withdrawAuditService.removeByIds(idList));
    }
}

package com.hjh.erp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hjh.common.core.domain.entity.SysUser;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.poi.ExcelUtil;
import com.hjh.system.mapper.SysUserMapper;
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
import com.hjh.erp.domain.SystemInfo;
import com.hjh.erp.service.ISystemInfoService;

/**
 * 系统日志Controller
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@RestController
@RequestMapping("/systemInfo/systemInfo")
public class SystemInfoController extends BaseController {
    @Autowired
    private ISystemInfoService systemInfoService;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询系统日志列表
     */
    @PreAuthorize("@ss.hasPermi('erp:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(SystemInfo systemInfo) {
        startPage();
        List<SystemInfo> page = systemInfoService.selectSystemInfoList(systemInfo);
        return getDataTable(page);
    }

    /**
     * 导出系统日志列表
     */
    @PreAuthorize("@ss.hasPermi('erp:info:export')")
    @Log(title = "系统日志" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SystemInfo systemInfo) {
        List<SystemInfo> list = systemInfoService.selectSystemInfoList(systemInfo);
        ExcelUtil<SystemInfo> util = new ExcelUtil<SystemInfo>(SystemInfo. class);
        return util.exportExcel(list, "系统日志数据");
    }

    /**
     * 获取系统日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(systemInfoService.getById(id));
    }

    /**
     * 新增系统日志
     */
    @PreAuthorize("@ss.hasPermi('erp:info:add')")
    @Log(title = "系统日志" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SystemInfo systemInfo) {
        return toAjax(systemInfoService.save(systemInfo));
    }

    /**
     * 修改系统日志
     */
    @PreAuthorize("@ss.hasPermi('erp:info:edit')")
    @Log(title = "系统日志" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SystemInfo systemInfo,String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign,sysUser.getGoogleSignCode())){
            return AjaxResult.error("谷歌验证码错误");
        }

        return AjaxResult.toAjax(systemInfoService.updateById(systemInfo));
    }

    /**
     * 删除系统日志
     */
    @PreAuthorize("@ss.hasPermi('erp:info:remove')")
    @Log(title = "系统日志" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(systemInfoService.removeByIds(idList));
    }
}

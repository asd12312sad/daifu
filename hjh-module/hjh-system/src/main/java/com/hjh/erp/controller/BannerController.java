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
import com.hjh.erp.domain.Banner;
import com.hjh.erp.service.IBannerService;

/**
 * 轮播图Controller
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@RestController
@RequestMapping("/banner/banner")
public class BannerController extends BaseController {
    @Autowired
    private IBannerService bannerService;

    /**
     * 查询轮播图列表
     */
    @PreAuthorize("@ss.hasPermi('banner:banner:list')")
    @GetMapping("/list")
    public TableDataInfo list(Banner banner) {
        startPage();
        List<Banner> page = bannerService.selectBannerList(banner);
        return getDataTable(page);
    }

    /**
     * 导出轮播图列表
     */
    @PreAuthorize("@ss.hasPermi('banner:banner:export')")
    @Log(title = "轮播图" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Banner banner) {
        List<Banner> list = bannerService.selectBannerList(banner);
        ExcelUtil<Banner> util = new ExcelUtil<Banner>(Banner. class);
        return util.exportExcel(list, "轮播图数据");
    }

    /**
     * 获取轮播图详细信息
     */
    @PreAuthorize("@ss.hasPermi('banner:banner:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(bannerService.getById(id));
    }

    /**
     * 新增轮播图
     */
    @PreAuthorize("@ss.hasPermi('banner:banner:add')")
    @Log(title = "轮播图" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Banner banner) {
        return toAjax(bannerService.save(banner));
    }

    /**
     * 修改轮播图
     */
    @PreAuthorize("@ss.hasPermi('banner:banner:edit')")
    @Log(title = "轮播图" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Banner banner) {
        return AjaxResult.toAjax(bannerService.updateById(banner));
    }

    /**
     * 删除轮播图
     */
    @PreAuthorize("@ss.hasPermi('banner:banner:remove')")
    @Log(title = "轮播图" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(bannerService.removeByIds(idList));
    }
}

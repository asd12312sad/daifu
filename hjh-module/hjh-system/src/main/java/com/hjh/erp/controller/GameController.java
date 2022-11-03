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
import com.hjh.erp.domain.Game;
import com.hjh.erp.service.IGameService;

/**
 * 游戏Controller
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@RestController
@RequestMapping("/erp/game")
public class GameController extends BaseController {
    @Autowired
    private IGameService gameService;

    /**
     * 查询游戏列表
     */
    @PreAuthorize("@ss.hasPermi('erp:game:list')")
    @GetMapping("/list")
    public TableDataInfo list(Game game) {
        startPage();
        List<Game> page = gameService.selectGameList(game);
        return this.getDataTable(page);
    }

    /**
     * 导出游戏列表
     */
    @PreAuthorize("@ss.hasPermi('erp:game:export')")
    @Log(title = "游戏" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Game game) {
        List<Game> list = gameService.selectGameList(game);
        ExcelUtil<Game> util = new ExcelUtil<Game>(Game. class);
        return util.exportExcel(list, "游戏数据");
    }

    /**
     * 获取游戏详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:game:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(gameService.getById(id));
    }

    /**
     * 新增游戏
     */
    @PreAuthorize("@ss.hasPermi('erp:game:add')")
    @Log(title = "游戏" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Game game) {
        return toAjax(gameService.save(game));
    }

    /**
     * 修改游戏
     */
    @PreAuthorize("@ss.hasPermi('erp:game:edit')")
    @Log(title = "游戏" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Game game) {
        return AjaxResult.toAjax(gameService.updateById(game));
    }

    /**
     * 删除游戏
     */
    @PreAuthorize("@ss.hasPermi('erp:game:remove')")
    @Log(title = "游戏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(gameService.removeByIds(idList));
    }
}

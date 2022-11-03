package com.hjh.erp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.SecurityUtils;
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
import com.hjh.erp.domain.UcUser;
import com.hjh.erp.service.IUcUserService;

/**
 * 会员Controller
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@RestController
@RequestMapping("/erp/user")
public class UcUserController extends BaseController {
    @Autowired
    private IUcUserService ucUserService;

    /**
     * 查询会员列表
     */
    @PreAuthorize("@ss.hasPermi('erp:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(UcUser ucUser) {
        startPage();
        List<UcUser> page = ucUserService.selectUcUserList(ucUser);
        return getDataTable(page);
    }



    /**
     * 获取会员详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:user:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(ucUserService.getById(id));
    }

    /**
     * 新增会员
     */
    @PreAuthorize("@ss.hasPermi('erp:user:add')")
    @Log(title = "会员" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UcUser ucUser) {


        //判断账号是否存在
        Long count = this.ucUserService.count(new QueryWrapper<UcUser>().eq("account", ucUser.getAccount()));
        if (count > 0) {
            return AjaxResult.error(400, "账号已存在");
        }
        //判断密码是否太简单
        if (ucUser.getAccount().length() < 6) {
            return AjaxResult.error(400, "账户不得低于六位");
        }
        //判断密码是否太简单
        if (ucUser.getPassword().length() < 6) {
            return AjaxResult.error(400, "密码不得低于六位");
        }
        //判断TRC地址T开头 必须等于34位
        if (!ucUser.getTrcAddress().startsWith("T") || ucUser.getTrcAddress().length() != 34) {
            return AjaxResult.error(400, "TRC地址不正确");
        }
        ucUser.setSalt(SecurityUtils.getSalt());
        ucUser.setPassword(SecurityUtils.encryptPassword(ucUser.getPassword()+ucUser.getSalt()));

        return toAjax(ucUserService.save(ucUser));
    }

    /**
     * 修改会员
     */
    @PreAuthorize("@ss.hasPermi('erp:user:edit')")
    @Log(title = "会员" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UcUser ucUser) {
        return AjaxResult.toAjax(ucUserService.updateById(ucUser));
    }

    /**
     * 删除会员
     */
    @PreAuthorize("@ss.hasPermi('erp:user:remove')")
    @Log(title = "会员" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(ucUserService.removeByIds(idList));
    }
}

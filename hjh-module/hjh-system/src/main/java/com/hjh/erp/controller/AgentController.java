package com.hjh.erp.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hjh.common.core.domain.entity.SysUser;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.SecurityUtils;
import com.hjh.common.utils.ServletUtils;
import com.hjh.common.utils.ip.IpUtils;
import com.hjh.erp.domain.Merchant;
import com.hjh.erp.service.impl.GoogleAuthenticator;
import com.hjh.system.mapper.SysUserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hjh.common.annotation.Log;
import com.hjh.common.core.controller.BaseController;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.enums.BusinessType;
import com.hjh.erp.domain.Agent;
import com.hjh.erp.service.IAgentService;

/**
 * 代理Controller
 *
 * @author xiaobing
 * @date 2022-07-09 22:39:14
 */
@RestController
@RequestMapping("/erp/agent")
public class AgentController extends BaseController {
    @Autowired
    private IAgentService agentService;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询代理列表
     */
    @PreAuthorize("@ss.hasPermi('erp:agent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Agent agent) {
        startPage();
        List<Agent> page = agentService.selectAgentList(agent);
        return getDataTable(page);
    }
    /**
     * 重置谷歌验证码
     */
    @PreAuthorize("@ss.hasPermi('erp:agent:list')")
    @GetMapping("/updateGoogleSignCode")
    public AjaxResult updateGoogleSignCode(String id,String adminGoogleSign) {
             SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());

            if (!isGoogle(adminGoogleSign,sysUser.getGoogleSignCode())){
            return AjaxResult.error("谷歌验证码错误");
        }        UpdateWrapper<Agent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("google_sign_code", GoogleAuthenticator.genSecret());
        updateWrapper.set("last_login_date",null);
        updateWrapper.eq("id",id);
        return toAjax( agentService.update(updateWrapper));
    }


    /**
     * 获取代理详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:agent:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {

        Agent byId = agentService.getById(id);
        byId.setPassword(null);
        return AjaxResult.success(byId);
    }

    /**
     * 新增代理
     */
    @PreAuthorize("@ss.hasPermi('erp:agent:add')")
    @Log(title = "代理" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Agent agent,@RequestParam String adminGoogleSign) {


             SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        logger.error("adminGoogleSign:"+adminGoogleSign);
        logger.error("sysUser.getGoogleSignCode():"+sysUser.getGoogleSignCode());

        if (!isGoogle(adminGoogleSign,sysUser.getGoogleSignCode())){
                return AjaxResult.error("谷歌验证码错误");
            }
        agent.setPassword(SecurityUtils.encryptPassword(agent.getPassword()));
        agent.setBalance(BigDecimal.ZERO);
        boolean save = agentService.save(agent);

        String inviteCode =  Integer.toHexString(agent.getId().intValue());

        //inviteCode不足6位，前面补0
        if(inviteCode.length() < 6){
            StringBuilder sb = new StringBuilder(inviteCode);
            for(int i = 0; i < 6 - inviteCode.length(); i++){
                sb.insert(0, "0");
            }
            inviteCode = sb.toString();
        }
        agent.setInviteCode(inviteCode);
        agent.setWithdrawAmount(BigDecimal.ZERO);
        agent.setUnbalancedAmount(BigDecimal.ZERO);

        agent.setCreateTime(new Date());
        return toAjax(agentService.updateById(agent));
    }


    /**
     * 修改代理
     */
    @PreAuthorize("@ss.hasPermi('erp:agent:edit')")
    @Log(title = "代理" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Agent agent, String adminGoogleSign) {
        Agent byId = agentService.getById(agent.getId());

             SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign,sysUser.getGoogleSignCode())){
            return AjaxResult.error("谷歌验证码错误");
        }
        agent.setPassword(byId.getPassword());
        agent.setUsdtAddress(byId.getUsdtAddress());

        return AjaxResult.toAjax(agentService.updateById(agent));
    }

    /**
     * 删除代理
     */
    @PreAuthorize("@ss.hasPermi('erp:agent:remove')")
    @Log(title = "代理" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids,String adminGoogleSign) {

        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign,sysUser.getGoogleSignCode())){
            return AjaxResult.error("谷歌验证码错误");
        }
        List<Long> idList = new ArrayList<>(ids.length);
        Collections.addAll(idList, ids);
        return AjaxResult.toAjax(agentService.removeByIds(idList));
    }


}

package com.ms.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.*;
import com.ms.merchant.mapper.*;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/role")
public class MemberRoleController {

    @Autowired
    private MerchantRoleMapper roleMapper;

    @Autowired
    private MerchantUserMapper userMapper;

    @Autowired
    private MerchantUserRoleMapper merchantUserRoleMapper;
    @Autowired
    private MerchantRoleMenuMapper merchantRoleMenuMapper;

    @Autowired
    private MerchantMenuMapper menuMapper;
    @GetMapping("/page")
    @MerchantApiNameAnnotation(apiName = "查询角色列表")
    public R page(MerchantRole role, Integer pageSize, Integer pageNumber){
        IPage<MerchantRole> page = new Page<>(pageNumber,pageSize);
        QueryWrapper<MerchantRole> queryWrapper = new QueryWrapper<>();
        if (role.getRoleName() != null && !role.getRoleName().equals("")) {
            queryWrapper.like("role_name", role.getRoleName());
        }
        queryWrapper.eq("merchant_id", CommonRequestHolder.getCurrentUserId());
        IPage<MerchantRole> list = roleMapper.selectPage(page,queryWrapper);
        return R.success(list);
    }

    @GetMapping("/list")
    @MerchantApiNameAnnotation(apiName = "查询角色列表")
    public R list(){
        List<MerchantRole> list = roleMapper.selectList(new QueryWrapper<MerchantRole>().eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
        return R.success(list);
    }
    @GetMapping("/menu/list")
    @MerchantApiNameAnnotation(apiName = "查询菜单列表")
    public R menuList(){
        List<MerchantMenu> list = menuMapper.selectList(new QueryWrapper<>());
        return R.success(list);
    }
    /**
     * 新增角色
     */
    @PostMapping("add")
    @MerchantApiNameAnnotation(apiName = "新增角色")
    public R add( @RequestBody MerchantRole role ) {


        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(role.getGoogleSign(), merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        if (role.getRoleId()==null) {
            if ("管理员".equals(role.getRoleName())){
                return R.failed(401, "管理员不能新增");
            }

            role.setMerchantId(CommonRequestHolder.getCurrentUserId().longValue());
            roleMapper.insert(role);
            List<MerchantMenu> bindingMerchantMenuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().in("menu_id", role.getMenuIds()));
            for (MerchantMenu merchantMenu : bindingMerchantMenuList) {
                MerchantRoleMenu merchantRoleMenu = new MerchantRoleMenu();
                merchantRoleMenu.setRoleId(role.getRoleId());
                merchantRoleMenu.setMenuId(merchantMenu.getMenuId());
                merchantRoleMenuMapper.insert(merchantRoleMenu);
            }
            return R.success();
        }else{
            MerchantRole merchantRole = roleMapper.selectById(role.getRoleId());
            if ("管理员".equals(merchantRole.getRoleName())){
                return R.failed(401, "管理员不能修改");
            }
            merchantRoleMenuMapper.delete(new QueryWrapper<MerchantRoleMenu>().eq("role_id", role.getRoleId()));
            List<MerchantMenu> bindingMerchantMenuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().in("menu_id", role.getMenuIds()));
            for (MerchantMenu merchantMenu : bindingMerchantMenuList) {
                MerchantRoleMenu merchantRoleMenu = new MerchantRoleMenu();
                merchantRoleMenu.setRoleId(role.getRoleId());
                merchantRoleMenu.setMenuId(merchantMenu.getMenuId());
                merchantRoleMenuMapper.insert(merchantRoleMenu);
            }

            return R.success(roleMapper.updateById(role));
        }
    }
    //删除角色
    @MerchantApiNameAnnotation(apiName = "删除角色")
    @GetMapping("delete")
    public R delete(@RequestParam Long roleId,@RequestParam String googleSign) {
        MerchantRole merchantRole = roleMapper.selectById(roleId);
        if ("管理员".equals(merchantRole.getRoleName())){
            return R.failed(401, "管理员不能删除");
        }



        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        Long count = merchantUserRoleMapper.selectCount(new QueryWrapper<MerchantUserRole>().eq("role_id", roleId));
        if (count>0){
            return R.failed("该角色已被使用，不能删除");
        }
        return R.success(roleMapper.deleteById(roleId));
    }

    /**
     * 新增角色
     */
    @MerchantApiNameAnnotation(apiName = "新增角色")
    @GetMapping("detail")
    public R detail( Long id) {
        MerchantRole merchantRole = roleMapper.selectById(id);


        List<MerchantRoleMenu> menuList = merchantRoleMenuMapper.selectList(new QueryWrapper<MerchantRoleMenu>().eq("role_id", id));

        if (menuList == null || menuList.size() == 0) {
            merchantRole.setBindingMerchantMenuList(new ArrayList<>());

            merchantRole.setNotBindingMerchantMenuList( menuMapper.selectList(new QueryWrapper<>()));

            return R.success(merchantRole);
        }
        //获取menuId集合
        List<Long> menuIdList = menuList.stream().map(MerchantRoleMenu::getMenuId).collect(java.util.stream.Collectors.toList());
        List<MerchantMenu> bindingMerchantMenuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().in("menu_id", menuIdList));
        List<MerchantMenu> notBindingMerchantMenuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().notIn("menu_id", menuIdList));
        merchantRole.setBindingMerchantMenuList(bindingMerchantMenuList);
        merchantRole.setNotBindingMerchantMenuList(notBindingMerchantMenuList);
        return R.success(merchantRole);
    }

    /**
     * 更新角色菜单绑定
     */
    @MerchantApiNameAnnotation(apiName = "更新角色菜单绑定")
    @PostMapping("building")
    public R building(@RequestBody MerchantRole updateData) {

        MerchantRole merchantRole = roleMapper.selectById(updateData.getRoleId());
        if ("管理员".equals(merchantRole.getRoleName())){
            return R.failed(401, "管理员不能修改");
        }
        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(updateData.getGoogleSign(), merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        merchantRoleMenuMapper.delete(new QueryWrapper<MerchantRoleMenu>().eq("role_id", updateData.getRoleId()));
        List<MerchantMenu> bindingMerchantMenuList = menuMapper.selectList(new QueryWrapper<MerchantMenu>().in("menu_id", updateData.getMenuIds()));
        for (MerchantMenu merchantMenu : bindingMerchantMenuList) {
            MerchantRoleMenu merchantRoleMenu = new MerchantRoleMenu();
            merchantRoleMenu.setRoleId(updateData.getRoleId());
            merchantRoleMenu.setMenuId(merchantMenu.getMenuId());
            merchantRoleMenuMapper.insert(merchantRoleMenu);
        }
        return R.success();
    }
}

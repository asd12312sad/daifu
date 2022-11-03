package com.ms.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.common.utils.SecurityUtils;
import com.ms.merchant.domain.*;
import com.ms.merchant.mapper.*;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/user")
public class MemberUserController {
    @Autowired
    private MerchantUserMapper userMapper;
    @Autowired
    private MerchantRoleMapper roleMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantUserRoleMapper merchantUserRoleMapper;

    @MerchantApiNameAnnotation(apiName = "查询账号列表")
    @GetMapping("/page")
    public R page(MerchantUser user, Integer pageSize, Integer pageNumber){
        IPage<MerchantUser> page = new Page<>(pageNumber,pageSize);
        QueryWrapper<MerchantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", CommonRequestHolder.getCurrentUserId());
        if (user.getUserName() != null && !user.getUserName().equals("")) {
            queryWrapper.like("user_name", user.getUserName());
        }
        if (user.getNickName() != null && !user.getNickName().equals("")) {
            queryWrapper.like("nick_name", user.getNickName());
        }
        IPage<MerchantUser> list = userMapper.selectPage(page,queryWrapper);
        for (MerchantUser merchantUser : list.getRecords()) {
            merchantUser.setPassword(null);
        }
        return R.success(list);
    }


    /**
     * 新增角色
     */
    @MerchantApiNameAnnotation(apiName = "新增用户")
    @PostMapping("add")
    public R add( @RequestBody MerchantUser merchantUser) {



        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(merchantUser.getGoogleSign(), loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }


        if (merchantUser.getUserId()==null) {

            if ("管理员".equals(merchantUser.getNickName())){
                return R.failed(401, "管理员不能新增");
            }

            Merchant merchantOne = merchantMapper.selectOne(new QueryWrapper<Merchant>().eq("account", merchantUser.getUserName()));
            Long count = userMapper.selectCount(new QueryWrapper<MerchantUser>().eq("user_name", merchantUser.getUserName()));
            if (count>0||merchantOne!=null) {
                return R.failed(401, "该账号已被注册");
            }
            merchantUser.setNickName(
                    merchantUser.getUserName()
            );
            merchantUser.setGoogleSignCode(GoogleAuthenticator.genSecret());
            merchantUser.setPassword(SecurityUtils.encryptPassword(merchantUser.getPassword()));
            merchantUser.setMerchantId(CommonRequestHolder.getCurrentUserId().longValue());
            userMapper.insert(merchantUser);
            if (merchantUser.getRoleIds() == null||merchantUser.getRoleIds().size()==0) {
                return R.success();

            }
            List<MerchantRole> bindingMerchantRoleList = roleMapper.selectList(new QueryWrapper<MerchantRole>().in("role_id", merchantUser.getRoleIds()));
            for (MerchantRole merchantRole : bindingMerchantRoleList) {
                MerchantUserRole merchantUserRole = new MerchantUserRole();
                merchantUserRole.setUserId(merchantUser.getUserId());
                merchantUserRole.setRoleId(merchantRole.getRoleId());
                merchantUserRoleMapper.insert(merchantUserRole);
            }
            return R.success();
        }else{
            MerchantUser updateData = userMapper.selectById(merchantUser.getUserId());
            if ("管理员".equals(updateData.getNickName())){
                return R.failed(401, "管理员不能修改");
            }
            if (!updateData.getUserName().equals(merchantUser.getUserName())){
                Merchant merchantOne = merchantMapper.selectOne(new QueryWrapper<Merchant>().eq("account", merchantUser.getUserName()));
                Long count = userMapper.selectCount(new QueryWrapper<MerchantUser>().eq("user_name", merchantUser.getUserName()));
                if (count>0||merchantOne!=null) {
                    return R.failed(401, "该账号已被注册");
                }
            }
            if (merchantUser.getPassword()!=null){
                merchantUser.setPassword(SecurityUtils.encryptPassword(merchantUser.getPassword()));
            }
            userMapper.updateById(merchantUser);
            if (merchantUser.getRoleIds() == null||merchantUser.getRoleIds().size()==0) {
                return R.success();
            }
            merchantUserRoleMapper.delete(new QueryWrapper<MerchantUserRole>().eq("user_id", merchantUser.getUserId()));
            List<MerchantRole> bindingMerchantRoleList = roleMapper.selectList(new QueryWrapper<MerchantRole>().in("role_id", merchantUser.getRoleIds()));
            for (MerchantRole merchantRole : bindingMerchantRoleList) {
                MerchantUserRole merchantUserRole = new MerchantUserRole();
                merchantUserRole.setUserId(merchantUser.getUserId());
                merchantUserRole.setRoleId(merchantRole.getRoleId());
                merchantUserRoleMapper.insert(merchantUserRole);
            }
            return R.success();
        }
    }
    //删除角色
    @MerchantApiNameAnnotation(apiName = "删除用户")
    @GetMapping("delete")
    public R delete(@RequestParam Long userId,@RequestParam String googleSign) {



        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }


        MerchantUser merchantUser = userMapper.selectById(userId);
        if ("管理员".equals(merchantUser.getNickName())){
            return R.failed(401, "管理员不能删除");
        }
        return R.success(userMapper.deleteById(userId));
    }

    /**
     * 新增角色
     */
    @MerchantApiNameAnnotation(apiName = "查询账号明细")
    @GetMapping("detail")
    public R detail( Long id) {
        MerchantUser merchantUser = userMapper.selectById(id);
        merchantUser.setPassword(null);
        List<MerchantUserRole> menuList = merchantUserRoleMapper.selectList(new QueryWrapper<MerchantUserRole>().eq("user_id", id));
        if (menuList == null || menuList.size() == 0) {
            merchantUser.setBindingMerchantRoleList(new ArrayList<>());
            merchantUser.setNotBindingMerchantRoleList( roleMapper.selectList(new QueryWrapper<MerchantRole>().eq("merchant_id", CommonRequestHolder.getCurrentUserId())));
            return R.success(merchantUser);
        }
        //获取roleId集合
        List<Long> roleIds = menuList.stream().map(MerchantUserRole::getRoleId).collect(java.util.stream.Collectors.toList());
        List<MerchantRole> bindingMerchantRoleList = roleMapper.selectList(new QueryWrapper<MerchantRole>().in("role_id", roleIds).eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
        List<MerchantRole> notBindingMerchantRoleList = roleMapper.selectList(new QueryWrapper<MerchantRole>().notIn("role_id", roleIds).eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
        merchantUser.setBindingMerchantRoleList(bindingMerchantRoleList);
        merchantUser.setNotBindingMerchantRoleList(notBindingMerchantRoleList);
        return R.success(merchantUser);
    }

    /**
     * 更新角色菜单绑定
     */
    @MerchantApiNameAnnotation(apiName = "更新角色绑定")
    @PostMapping("building")
    public R building(@RequestBody MerchantUser updateData) {
        Merchant merchant = merchantMapper.selectById(CommonRequestHolder.getCurrentUserId());
        MerchantUser merchantUser = userMapper.selectById(updateData.getUserId());
        if ("管理员".equals(merchantUser.getNickName())){
            return R.failed(401, "管理员不能修改");
        }



        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(updateData.getGoogleSign(), loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        merchantUserRoleMapper.delete(new QueryWrapper<MerchantUserRole>().eq("user_id", updateData.getUserId()));
        List<MerchantRole> bindingMerchantRoleList = roleMapper.selectList(new QueryWrapper<MerchantRole>().in("role_id", updateData.getRoleIds()));


        for (MerchantRole merchantRole : bindingMerchantRoleList) {
            MerchantUserRole merchantUserRole = new MerchantUserRole();
            merchantUserRole.setUserId(updateData.getUserId());
            merchantUserRole.setRoleId(merchantRole.getRoleId());
            merchantUserRoleMapper.insert(merchantUserRole);
        }
        return R.success();
    }
}

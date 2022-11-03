package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.UcUserMapper;
import com.hjh.erp.domain.UcUser;
import com.hjh.erp.service.IUcUserService;

/**
 * 会员Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Service
public class UcUserServiceImpl extends ServiceImpl<UcUserMapper,UcUser> implements IUcUserService
{



    /**
     * 查询会员列表
     *
     * @param ucUser 会员
     * @return 会员集合
     */
    @Override
    public List<UcUser> selectUcUserList(UcUser ucUser){
       return  this.baseMapper.selectUcUserList(ucUser,new HashMap<>());

    }
}

package com.hjh.erp.service;

import com.hjh.erp.domain.UcUser;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 会员Service接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface IUcUserService  extends IService<UcUser>
{

    /**
     * 查询会员列表
     *
     * @param ucUser 会员
     * @return 会员集合
     */
    public List<UcUser> selectUcUserList(UcUser ucUser);

}

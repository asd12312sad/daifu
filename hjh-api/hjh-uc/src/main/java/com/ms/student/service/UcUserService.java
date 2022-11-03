package com.ms.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.common.domain.R;
import com.ms.common.domain.UpdatePassWordDto;
import com.ms.student.domain.UcUser;
import com.ms.student.domain.dto.LoginDto;
import com.ms.student.domain.dto.RegisterDto;
import com.ms.student.domain.vo.LoginVo;

/**
 * 用户信息
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/1/3
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/1/3              肖兵             v1.0.0           Created
 */
public interface UcUserService extends IService<UcUser> {

    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return token
     */
    R<LoginVo> login(LoginDto loginDto);

    /**
     * 获取账号数量
     * @param account 账号
     * @return 数量
     */
    Long selectCount(String account);

    /**
     * 修改密码
     * @param updatePassWordDto 新密码
     * @return 修改密码
     */
    R updatePassword(UpdatePassWordDto updatePassWordDto);

    /**
     * 更改头像
     * @param portrait  头像
     * @return 更改头像
     */
    Boolean updatePortrait(String portrait,Integer type);


    R register(RegisterDto registerDto);

    R<LoginVo> addressLogin(String address);
}

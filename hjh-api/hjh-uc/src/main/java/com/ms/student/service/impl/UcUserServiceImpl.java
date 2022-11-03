package com.ms.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.core.domain.LoginUserVo;
import com.ms.common.domain.R;
import com.ms.common.core.domain.ResultCode;
import com.ms.common.core.enums.UserType;
import com.ms.common.core.utils.GsonUtil;
import com.ms.common.core.utils.StringUtils;
import com.ms.common.domain.*;
import com.ms.common.utils.*;
import com.ms.student.domain.UcUser;
import com.ms.student.domain.dto.LoginDto;
import com.ms.student.domain.dto.RegisterDto;
import com.ms.student.domain.vo.LoginVo;
import com.ms.student.mapper.*;
import com.ms.student.service.UcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户信息
 *
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/1/3
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/1/3              肖兵             v1.0.0           Created
 */
@Service
public class UcUserServiceImpl extends ServiceImpl<UcUserMapper, UcUser> implements UcUserService {

    @Autowired
    private SmsUtil smsUtil;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户登录
     *
     * @param loginDto 登录信息
     * @return token
     */
    @Override
    public R<LoginVo> login(LoginDto loginDto) {
        String account = loginDto.getAccount();

        UcUser ucUser = this.getOne(new QueryWrapper<UcUser>().eq("account", account));
        if (ucUser == null) {
            return R.failed(400, "用户名或密码不正确");
        }
        boolean flag = SecurityUtils.matchesPassword(loginDto.getPassword() + ucUser.getSalt(), ucUser.getPassword());
        if (!flag) {
            return R.failed(400, "用户名或密码不正确");
        }
        if (ucUser.getStatus() == 0) {
            return R.failed(400, "账号已被禁用");
        }
        // 登录token
        long timeMillis = System.currentTimeMillis();
        String token = JWTUtil.getInstance().generateToken(ucUser.getId() + "", ucUser.getUsername(), ucUser.getAccount(), 604800, UserType.STUDENT.getCode(), timeMillis);


        //封装redis对象
        LoginUserVo redisUserVo = new LoginUserVo();
        redisUserVo.setName(ucUser.getUsername());
        redisUserVo.setId(ucUser.getId());
        redisUserVo.setParentId(ucUser.getId());
        redisUserVo.setParentName(ucUser.getUsername());
        redisUserVo.setLoginTime(timeMillis);
        redisTemplate.opsForValue().set(ucUser.getId() + ucUser.getAccount() + ucUser.getUsername() + UserType.STUDENT.getCode(), GsonUtil.getJson(redisUserVo));
        String newToken = "Bearer " + token;

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(newToken);
        loginVo.setTokenType(UserType.STUDENT.getCode());
        ucUser.setLastLoginTime(new Date());
        if (ucUser.getLoginCount() == null) {
            ucUser.setLoginCount(0);
        }
        ucUser.setLoginCount(ucUser.getLoginCount() + 1);
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        ucUser.setIp(ip);

        this.updateById(ucUser);

        return R.success(loginVo);
    }

    @Override
    public Long selectCount(String account) {
        return this.baseMapper.selectCount(new QueryWrapper<UcUser>().eq("account", account));
    }

    @Override
    public R updatePassword(UpdatePassWordDto updatePassWordDto) {
        UcUser user = this.baseMapper.selectOne(new QueryWrapper<UcUser>().eq("account", updatePassWordDto.getAccount()));
        if (user == null) {
            return R.setToCode(ResultCode.REGISTRY_NOTHAS);
        }
        boolean flag = smsUtil.checkCode(updatePassWordDto.getAccount(), updatePassWordDto.getCode());
        if (!flag) {
            return R.failed(400, "用户名或验证码不正确");
        }
        user.setPassword(SecurityUtils.encryptPassword(updatePassWordDto.getPassword()));
        this.updateById(user);
        return R.success();

    }

    @Override
    public Boolean updatePortrait(String portrait, Integer type) {
        if (type == 1) {
            UcUser user = baseMapper.selectById(CommonRequestHolder.getCurrentUserId());
            user.setAvatar(portrait);
            return updateById(user);
        }
        UcUser user = baseMapper.selectById(CommonRequestHolder.getCurrentUserParentId());
        user.setAvatar(portrait);
        return updateById(user);
    }

    @Override
    public R register(RegisterDto registerDto) {
        //判断账号是否存在
        Long count = this.baseMapper.selectCount(new QueryWrapper<UcUser>().eq("account", registerDto.getAccount()));
        if (count > 0) {
            return R.failed(400, "账号已存在");
        }
        //判断密码是否太简单
        if (registerDto.getAccount().length() < 6) {
            return R.failed(400, "账户不得低于六位");
        }
        UcUser user = baseMapper.selectOne(new QueryWrapper<UcUser>().eq("trc_address", registerDto.getTrcAddress()));
        if (user != null) {
            return R.failed(400, "trc_address已存在");
        }
        //判断密码是否太简单
        if (registerDto.getPassword().length() < 6) {
            return R.failed(400, "密码不得低于六位");
        }
        //判断TRC地址T开头 必须等于34位
        if (!registerDto.getTrcAddress().startsWith("T") || registerDto.getTrcAddress().length() != 34) {
            return R.failed(400, "TRC地址不正确");
        }

         user = new UcUser();

        user.setAccount(registerDto.getAccount());
        user.setUsername(registerDto.getAccount());
        user.setSalt(SecurityUtils.getSalt());
        user.setPassword(SecurityUtils.encryptPassword(registerDto.getPassword() + user.getSalt()));
        user.setTrcAddress(registerDto.getTrcAddress());
        user.setRegistrationTime(new Date());
        user.setStatus(1);

        //判断邀请码是否存在
        if (!StringUtils.isEmpty(registerDto.getInviteCode())) {
            UcUser ucUser = this.baseMapper.selectOne(new QueryWrapper<UcUser>().eq("id", registerDto.getInviteCode()));
            if (ucUser == null) {
                return R.failed(400, "邀请码不存在");
            }
            user.setInviterId(ucUser.getId());
        }
        this.save(user);
        return R.success();
    }
    @Override
    public R<LoginVo> addressLogin(String address) {

        UcUser ucUser = this.getOne(new QueryWrapper<UcUser>().eq("trc_address", address));
        if (ucUser == null) {
            ucUser = this.addressRegister(address);
        }
        if (ucUser.getStatus() == 0) {
            return R.failed(400, "账号已被禁用");
        }
// 登录token
        long timeMillis = System.currentTimeMillis();
        String token = JWTUtil.getInstance().generateToken(ucUser.getId() + "", ucUser.getUsername(), ucUser.getAccount(), 604800, UserType.STUDENT.getCode(), timeMillis);
//封装redis对象
        LoginUserVo redisUserVo = new LoginUserVo();
        redisUserVo.setName(ucUser.getUsername());
        redisUserVo.setId(ucUser.getId());
        redisUserVo.setParentId(ucUser.getId());
        redisUserVo.setParentName(ucUser.getUsername());
        redisUserVo.setLoginTime(timeMillis);
        redisTemplate.opsForValue().set(ucUser.getId() + ucUser.getAccount() + ucUser.getUsername() + UserType.STUDENT.getCode(), GsonUtil.getJson(redisUserVo));
        String newToken = "Bearer " + token;
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(newToken);
        loginVo.setTokenType(UserType.STUDENT.getCode());
        ucUser.setLastLoginTime(new Date());
        if (ucUser.getLoginCount() == null) {
            ucUser.setLoginCount(0);
        }
        ucUser.setLoginCount(ucUser.getLoginCount() + 1);
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        ucUser.setIp(ip);

        this.updateById(ucUser);

        return R.success(loginVo);
    }

    private UcUser addressRegister(String address) {


        UcUser user = new UcUser();

        user.setAccount(address);
        user.setUsername(address);
        user.setSalt(SecurityUtils.getSalt());
        user.setPassword(SecurityUtils.encryptPassword(address + user.getSalt()));
        user.setTrcAddress(address);
        user.setRegistrationTime(new Date());
        user.setStatus(1);
        this.save(user);

        return user;
    }

}

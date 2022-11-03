package com.hjh.framework.web.service;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.hjh.common.constant.WeChatConstants;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.utils.*;
import com.hjh.common.utils.WeChatHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.hjh.common.constant.Constants;
import com.hjh.common.core.domain.entity.SysUser;
import com.hjh.common.core.domain.model.LoginUser;
import com.hjh.common.core.redis.RedisCache;
import com.hjh.common.exception.CustomException;
import com.hjh.common.exception.user.CaptchaException;
import com.hjh.common.exception.user.CaptchaExpireException;
import com.hjh.common.exception.user.UserPasswordNotMatchException;
import com.hjh.common.utils.ip.IpUtils;
import com.hjh.framework.manager.AsyncManager;
import com.hjh.framework.manager.factory.AsyncFactory;
import com.hjh.system.service.ISysConfigService;
import com.hjh.system.service.ISysUserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录校验方法
 *
 * @author hjh
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid,String googleSignCode) {
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, code, uuid);
        }
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Boolean authcode = GoogleAuthenticator.authcode(googleSignCode, loginUser.getUser().getGoogleSignCode());
        if (!authcode) {
            throw new CustomException("谷歌验证码错误");
        }
        recordLoginInfo(loginUser.getUser());

        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(SysUser user) {
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(user);
    }


}

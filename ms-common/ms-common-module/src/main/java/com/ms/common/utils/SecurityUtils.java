package com.ms.common.utils;

import com.ms.common.core.constant.CacheConstants;
import com.ms.common.core.utils.text.Convert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 权限获取工具类
 *
 * @author xiaobing
 */
public class SecurityUtils {


    /**
     * 获取用户
     */
    public static String getUsername() {
        String header = ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_USERNAME);
        try {
            String name = URLDecoder.decode(header, "utf-8");
            return name;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return header;

    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return Convert.toLong(ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_USER_ID));
    }


    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    public static String getTokenType() {
        return ServletUtils.getRequest().getHeader(CacheConstants.TOKEN_TYPE);


    }

    public static String getSalt() {

        //随机生成盐值
        return String.valueOf(Math.random());
    }
}

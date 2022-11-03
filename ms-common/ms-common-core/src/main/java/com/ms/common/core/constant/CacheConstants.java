package com.ms.common.core.constant;

/**
 * 缓存的key 常量
 *
 * @author xiaobing
 */
public class CacheConstants {
    /**
     * 令牌自定义标识
     */
    public static final String HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";


    /**
     * 用户ID字段
     */
    public static final String TOKEN_TYPE = "token_type";

    /**
     * 是否为管理员
     */
    public static final String IF_ADMIN = "if_admin";

    /**
     * oauth 客户端信息
     */
    public static final String CLIENT_DETAILS_KEY = "oauth:client:details";

    /**
     * access_token
     */
    public static final String ACCESS_TOKEN = "access_token";
}

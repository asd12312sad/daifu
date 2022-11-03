package com.ms.common.utils;


import com.ms.common.core.domain.ResultCode;
import com.ms.common.core.utils.GsonUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 单利模式
 * 使用HS512加密算法
 * @author xiaobing
 * @date 2020-02-29 10:34
 * @version v1.0.0
 * @Description
 *  单例（饿汉）模式
 *
 * Modification History:
 * Date                 Author          Version          Description
---------------------------------------------------------------------------------*
 * 2020-02-29 10:34     xiaobing          v1.0.0           Created
 *
 */
public class JWTUtil {


    private static JWTUtil instance = new JWTUtil();

    private JWTUtil() { }

    public static JWTUtil getInstance() {
        return instance;
    }


    /**
     * 生成JWT TOKEN
     *
     * @param userId 需要保存的用户ID，字符串类型，也可以保存其他的想要保存的东西
     * @param secret 密钥，加密算法需要，对称加密
     * @param exp    过期时间，单位分钟
     * @return 生成的JWT TOKEN
     */
    public String generateToken(String userId, String userName,String phone, int exp, String secret,Long loginTime) {

        HashMap<String, Object> map = new HashMap<>();
        //也可以放置一些其他的参数
        map.put("userId", userId);
        map.put("loginTime", loginTime);
        map.put("userName", userName);
        map.put("phone",phone);
        GsonUtil.getJson(map);


        long endTime = System.currentTimeMillis() + 1000 * 60 * exp;

        String token = Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(endTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;

    }


    /**
     * 验证JWT TOKEN
     * @param token     要验证的TOKEN
     * @param secret    密钥，解密需要，对称解密
     * @return
     *      TOKEN验证成功返回JWTResult对象
     * @throws ExpiredJwtException  TOKEN过期抛错
     * @throws SignatureException   签名验证失败抛错
     */
    public JWTResult checkToken(String token, String secret) throws ExpiredJwtException, SignatureException {

        try {
            // parse the token.
            Map<String, Object> body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            return new JWTResult(true,(Long)body.get("loginTime"), (String) body.get("userId"),(String)body.get("userName"),(String)body.get("phone"), ResultCode.SUCCESS.getMsg(), ResultCode.SUCCESS.getCode()+"");

            //return new JWTResult(true, body.get("userId").toString(),body.get("unitId").toString(),body.get("username").toString(), ResultCode.SUCCESS.getMsg(), ResultCode.SUCCESS.getCode());


        } catch (ExpiredJwtException e) {       // 若TOKEN过期则直接抛错
            e.printStackTrace();
            return new JWTResult(false,null,null,null, null, ResultCode.TOKEN_TIME_OUT.getMsg(), ResultCode.TOKEN_TIME_OUT.getCode()+"");
        } catch (SignatureException e) {        //若签名验证失败则直接抛错
            return new JWTResult(false,null,null,null,null,ResultCode.NO_AUTH_CODE.getMsg(), ResultCode.NO_AUTH_CODE.getCode()+"");
        }catch (Exception e) {
            e.printStackTrace();
            return new JWTResult(false, null,null,null,null,ResultCode.NO_AUTH_CODE.getMsg(), ResultCode.NO_AUTH_CODE.getCode()+"");
        }

    }


    /**
     * TOKEN解析内容封装
     *
     */
    public static class JWTResult {

        /**
         * TOKEN状态
         * 仅当TOKEN可用一切正常时为true
         * 其它情况均为false
         */
        private boolean status;

        /**
         * 用户ID
         */
        private String userId;

        private String userName;

        private Long loginTime;

        public Long getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(Long loginTime) {
            this.loginTime = loginTime;
        }

        /**
         * 错误信息，当status==false时该属性值有效
         */
        private String msg;
        private String phone;

        /**
         * 错误状态吗
         *  status == true  code == 0
         *  status == false code != 0
         */
        private String code;

        public JWTResult() {
            super();
        }

        public JWTResult(boolean status, Long loginTime, String userId, String userName,String phone, String msg, String code) {
            this.status = status;
            this.userId = userId;
            this.phone = phone;
            this.userName = userName;
            this.msg = msg;
            this.code = code;
            this.loginTime = loginTime;
        }


        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }


}

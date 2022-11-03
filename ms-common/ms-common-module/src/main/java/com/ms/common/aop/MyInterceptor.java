package com.ms.common.aop;

import java.io.*;
import java.util.Date;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ms.common.config.IgnoreUrlsConfig;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.core.annotation.SameUrlData;
import com.ms.common.core.annotation.TokenTypeAnnotation;
import com.ms.common.core.domain.LoginUserVo;
import com.ms.common.domain.R;
import com.ms.common.core.domain.ResultCode;
import com.ms.common.core.enums.UserType;
import com.ms.common.core.utils.GsonUtil;
import com.ms.common.core.utils.SpringUtils;
import com.ms.common.domain.RequestLog;
import com.ms.common.mapper.RequestLogDao;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.common.utils.IpUtils;
import com.ms.common.utils.JWTUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MyInterceptor implements HandlerInterceptor {


    private final RedisTemplate<String, String> redisCacheTemplate;
    private final IgnoreUrlsConfig ignoreUrlsConfig;

    public MyInterceptor(RedisTemplate<String, String> redisCacheTemplate,
                         IgnoreUrlsConfig ignoreUrlsConfig) {
        this.redisCacheTemplate = redisCacheTemplate;
        this.ignoreUrlsConfig = ignoreUrlsConfig;
    }


    /**
     * 用于拦截 Request Header 中的用户基础信息
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //白名单路径直接放行
        PathMatcher pathMatcher = new AntPathMatcher();

        /*
        swagger不需要进行拦截
         */
        String uri = request.getRequestURI();


        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, uri)) {
                return true;
            }
        }
        String requestMethod = request.getMethod();
        if ((!StringUtils.isEmpty(requestMethod)) && requestMethod.equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        String headerType = request.getHeader("TokenType");


        if (headerType == null || "".equals(headerType)) {
            // 若获取不到 TOKENTYPE 的存在
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            R rvl = R.setToCode(ResultCode.NO_AUTH_CODE);
            out.write(GsonUtil.getJson(rvl));
            out.flush();
            out.close();
            return Boolean.FALSE;
        }

//        if (headerType.equals(UserType.ADMIN.getCode())) {
//            String id = request.getHeader("id");
//
//
//            String name = URLDecoder.decode(request.getHeader("name"), "utf-8");
//            CommonRequestHolder.init(Long.parseLong(id), name);
//            return true;
//        }

        String header = request.getHeader("Authorization");
        if (StringUtils.isEmpty(header)) {
            header = request.getHeader("authorization");
        }
        if (header == null || !header.startsWith("Bearer ")) {
            //不需要登录也可以访问的地址
            if (uri.contains("/referral/info")) {
                CommonRequestHolder.isInit.set(false);
                return Boolean.TRUE;
            }
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            R rvl = R.setToCode(ResultCode.NO_AUTH_CODE);
            out.write(GsonUtil.getJson(rvl));
            out.flush();
            out.close();


            return Boolean.FALSE;

        }


        JWTUtil.JWTResult jwtResult;

        UserType secret = UserType.getByCode(headerType);
        //解析 TOKEN
        jwtResult = JWTUtil.getInstance().checkToken(header.replace("Bearer ", ""), secret.getCode());
        if (jwtResult.isStatus()) {
            String userId = jwtResult.getUserId();
            String phone = jwtResult.getPhone();
            String userName = jwtResult.getUserName();

            String s = userId + phone + userName + headerType;
            System.out.println(s);

            String userStr = redisCacheTemplate.opsForValue().get(userId + userName + phone + headerType);
            if (userStr == null) {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                R rvl = R.setToCode(ResultCode.TOKEN_TIME_OUT);
                out.write(GsonUtil.getJson(rvl));
                out.flush();
                out.close();
                return Boolean.FALSE;
            }
            redisCacheTemplate.opsForValue().set(userId + userName + phone + headerType, userStr, 30, TimeUnit.MINUTES);
            //将用户信息 JSON 串转换为对象
            LoginUserVo userVo = (LoginUserVo) GsonUtil.getObject(userStr, LoginUserVo.class);
            Long loginTime = jwtResult.getLoginTime();
            if (secret.getType().equals(UserType.TEACHER.getType())) {
                if (!userVo.getLoginTime().equals(loginTime)) {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    R rvl = R.setToCode(ResultCode.YOUR_ACCOUNT_HAS_BEEN_LOGGED_IN_ON_ANOTHER_DEVICE);
                    out.write(GsonUtil.getJson(rvl));
                    out.flush();
                    out.close();
                    return Boolean.FALSE;
                }
            }
            //初始化当前线程登陆用户状态
            CommonRequestHolder.init(userVo.getId(), userVo.getName(), userVo.getParentId(), userVo.getParentName());

            if (handler instanceof HandlerMethod) {
                SameUrlData tokenTypeAnnotation = findAnnotation((HandlerMethod) handler, SameUrlData.class);
                //没有声明需要权限,或者声明不验证权限
                if (tokenTypeAnnotation == null) {
                    return true;
                } else {
                    if (redisCacheTemplate.hasKey(CommonRequestHolder.getCurrentUserId() + uri)) {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        R rvl = R.setToCode(ResultCode.REPEAT_REQUEST);
                        out.write(GsonUtil.getJson(rvl));
                        out.flush();
                        out.close();
                        return Boolean.FALSE;
                    }
                    redisCacheTemplate.opsForValue().set(CommonRequestHolder.getCurrentUserId() + uri, "", 30, TimeUnit.SECONDS);
                    return true;
                }
            }


            if (handler instanceof HandlerMethod) {
                TokenTypeAnnotation tokenTypeAnnotation = findAnnotation((HandlerMethod) handler, TokenTypeAnnotation.class);
                //没有声明需要权限,或者声明不验证权限
                if (tokenTypeAnnotation == null) {
                    return true;
                } else {
                    //在这里实现自己的权限验证逻辑
                    if (!StringUtils.isEmpty(headerType)) {//如果验证成功返回true（这里直接写false来模拟验证失败的处理）
                        UserType[] userTypes = tokenTypeAnnotation.tokenTypeCan();
                        for (UserType userType : userTypes) {
                            secret.getCode().equals(userType.getCode());
                            return true;
                        }
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(GsonUtil.getJson(R.setToCode(ResultCode.INSUFFICIENT_PERMISSIONS)));
                        return false;
                    } else {//如果验证失败
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(GsonUtil.getJson(R.setToCode(ResultCode.INSUFFICIENT_PERMISSIONS)));
                        return false;
                    }
                }
            } else {
                return true;
            }


        }

        // 若 TOKEN 不合法
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        R rvl = R.setToCode(ResultCode.NO_AUTH_CODE);
        out.write(GsonUtil.getJson(rvl));
        out.flush();
        out.close();
        return Boolean.FALSE;

    }

    private <T extends Annotation> T findAnnotation(HandlerMethod handler, Class<T> annotationType) {
        T annotation = handler.getBeanType().getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        return handler.getMethodAnnotation(annotationType);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable
            Exception ex) {
        String uri = request.getRequestURI();
        if (handler instanceof HandlerMethod) {


            MerchantApiNameAnnotation apiNameAnnotation = findAnnotation((HandlerMethod) handler, MerchantApiNameAnnotation.class);


            //没有声明需要权限,或者声明不验证权限
            if (apiNameAnnotation != null) {
                String header = request.getHeader("Authorization");
                String headerType = request.getHeader("TokenType");
                UserType secret = UserType.getByCode(headerType);

                JWTUtil.JWTResult jwtResult = JWTUtil.getInstance().checkToken(header.replace("Bearer ", ""), secret.getCode());

                RequestLog requestLog = new RequestLog();
                requestLog.setName(apiNameAnnotation.apiName());
                requestLog.setMerchantId(Long.valueOf(jwtResult.getUserId()));
                RequestWrapper requestWrapper = new RequestWrapper(request);

                String jsonBody = requestWrapper.getBody();



                JSONObject jsonObject = JSONObject.parseObject(jsonBody);
                if (jsonObject == null) {
                    jsonObject = new JSONObject();
                }
                //获取请求RequestParams参数
                Map<String, String[]> params = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : params.entrySet()) {
                    String values = "";
                    for (String value : entry.getValue()) {
                        values = values + value + ",";
                    }
                    //去除最后一个逗号
                    values = values.substring(0, values.length() - 1);
                    jsonObject.put(entry.getKey(), values);

                }

                requestLog.setRequestLog(jsonObject.toJSONString());
                //获取返回值
                JSON.toJSONString(request.getAttribute("code"),
                        SerializerFeature.DisableCircularReferenceDetect,
                        SerializerFeature.WriteMapNullValue);
                //获取返回值
                String returnData = JSON.toJSONString(request.getAttribute("returnData"),
                        SerializerFeature.DisableCircularReferenceDetect,
                        SerializerFeature.WriteMapNullValue);

                JSONObject jsonObject1 = JSONObject.parseObject(returnData);
                Integer code = jsonObject1.getInteger("code");
                requestLog.setResponseLog(returnData);
                requestLog.setStatus(code==200?1:0);
                requestLog.setType(1);
                requestLog.setMerchantUserId(Long.valueOf(jwtResult.getUserName()));
                requestLog.setRequestTime(new Date());
                requestLog.setIp(IpUtils.getIpAddr(request));
                SpringUtils.getBean(RequestLogDao.class).insert(requestLog);
            }
        }

    }



}

package com.hjh.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.enums.ResultCode;
import com.hjh.common.enums.SmsTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 发送短信工具类
 *
 * @author xiaobing
 */
@Slf4j
@Service
public class SmsUtil {

    /**
     * redis 操作类
     */
    @Autowired
    private RedisTemplate redisTemplate;


    private static final String ACCESS_KEY_ID = "*";
    private static final String ACCESS_SECRET = "*";
    private static final String SIGN_NAME = "合家和家装设计";

    // 返回数字下标0为所发送的验证码 1为状态(OK为发送成功其他均为失败)
    public static String[] sendSms(String phone, Integer type,String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", SIGN_NAME);
        request.putQueryParameter("TemplateCode", SmsTemplateEnum.getTemplateByType(type));// 枚举type获取模板Code
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        String[] strArray = new String[2];
        try {
            CommonResponse response = client.getCommonResponse(request);
            JSONObject jsonObject = JSONObject.parseObject(response.getData());
            String str = (String) jsonObject.get("Code");
            strArray[0] = code;
            strArray[1] = str;
        } catch (ServerException e) {
            strArray[1] = "error";
            e.printStackTrace();
        } catch (ClientException e) {
            strArray[1] = "error";
            e.printStackTrace();
        } catch (Exception e) {
            strArray[1] = "error";
            e.printStackTrace();
        }
        return strArray;
    }

//    public static String getCode() {
//        String string = Integer.valueOf((int) ((Math.random() * 9 + 1) * 100000)).toString();
//        return string;
//
//    }

    // 如果发送成功将验证码存入redis
    public AjaxResult saveCodeToRedis(String phone, Integer smsType,String code) {

        boolean hasKey = redisTemplate.hasKey(phone + "code");
        if (!hasKey) {
            String[] strings = sendSms(phone, smsType,code);
            if ("OK".equals(strings[1])) {
                redisTemplate.opsForValue().set(phone + "code", strings[0] + "-" + System.currentTimeMillis(), 15, TimeUnit.MINUTES);
                return AjaxResult.success();
            } else if ("isv.BUSINESS_LIMIT_CONTROL".equals(strings[1])) {
                return AjaxResult.setToCode(ResultCode.BUSINESS_LIMIT_CONTROL);
            } else if ("isv.MOBILE_NUMBER_ILLEGAL".equals(strings[1])) {
                return AjaxResult.setToCode(ResultCode.PHONE_ERROR);
            } else {
                return AjaxResult.setToCode(ResultCode.SMS_ERROR);
            }
        } else {
            // 如果redis有上次发送的验证码，验证发送间隔是否到一分钟
            String str = (String) redisTemplate.opsForValue().get(phone + "code");
            String[] split = str.split("-");
            // 发送验证码时的时间戳
            long sendSmsTime = Long.valueOf(split[1]);
            // 现在的时间戳
            long nowTime = System.currentTimeMillis();
            if (nowTime - sendSmsTime < 60000) {
                return AjaxResult.setToCode(ResultCode.CODE_TIME);
            }
            // 删除之前发送的验证码使之失效
            deleteCode(phone);
            // 发送新验证码
            String[] strings = sendSms(phone, smsType,code);
            if ("OK".equals(strings[1])) {
                redisTemplate.opsForValue().set(phone + "code", strings[0] + "-" + System.currentTimeMillis(), 15, TimeUnit.MINUTES);
//                redisTemplate.opsForValue().set(phone + "code", "123456" + "-" + System.currentTimeMillis(), 15, TimeUnit.MINUTES);
                return AjaxResult.setToCode(ResultCode.SUCCESS);
            } else if ("isv.BUSINESS_LIMIT_CONTROL".equals(strings[1])) {
                return AjaxResult.setToCode(ResultCode.BUSINESS_LIMIT_CONTROL);
            } else if ("isv.MOBILE_NUMBER_ILLEGAL".equals(strings[1])) {
                return AjaxResult.setToCode(ResultCode.PHONE_ERROR);
            } else {
                return AjaxResult.setToCode(ResultCode.SMS_ERROR);
            }
        }
    }

    // 验证验证码是否正确
    public boolean checkCode(String phone, String code) {
        String str = (String) redisTemplate.opsForValue().get(phone + "code");
        if (str == null) {
            return false;
        }
        String[] split = str.split("-");
        return code.equals(split[0]);
    }

    // 验证码使用过后删除,使之不再生效
    public boolean deleteCode(String phone) {
        return redisTemplate.delete(phone + "code");
    }
}


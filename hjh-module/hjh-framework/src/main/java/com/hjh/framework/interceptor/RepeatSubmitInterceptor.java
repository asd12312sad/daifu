package com.hjh.framework.interceptor;

import java.lang.reflect.Method;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hjh.common.utils.ip.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.alibaba.fastjson.JSONObject;
import com.hjh.common.annotation.RepeatSubmit;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.utils.ServletUtils;

/**
 * 防止重复提交拦截器
 *
 * @author hjh
 */
@Component
public abstract class RepeatSubmitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ipAddr = IpUtils.getIpAddr(ServletUtils.getRequest());
        Boolean ipFlag = false;
        String[] split = ipAddr.split(", ");
        for (String s : split) {
            if (s.equals("47.243.60.184")){
                ipFlag = true;
            }
            if (s.equals("101.24.92.245")){
                ipFlag = true;
            }
            if (s.equals("47.243.80.99")){
                ipFlag = true;
            }
            if (s.equals("47.242.109.159")){
                ipFlag = true;
            }
            if (s.equals("47.243.92.47")){
                ipFlag = true;
            }
            if (s.equals("101.24.93.135")){
                ipFlag = true;
            }
            if (s.equals("183.198.132.154")){
                ipFlag = true;
            }

        }
        if (!ipFlag){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<html><head><title></title></head><body>");
            stringBuilder.append("出现异常IP操作情况").append(ipAddr);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            //multipart模式
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                mimeMessageHelper.setTo("xiahouv587@gmail.com");//收件人邮箱user.getMail()
                mimeMessageHelper.setFrom("barizkein@gmail.com");//发件人邮箱
                mimeMessage.setSubject("异常操作");
                //启用html
                mimeMessageHelper.setText(stringBuilder.toString(), true);
                javaMailSender.send(mimeMessage);
            } catch (MessagingException el) {
                el.printStackTrace();
            }

            throw new RuntimeException("操作异常");
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(request)) {
                    AjaxResult ajaxResult = AjaxResult.error("不允许重复提交，请稍后再试");
                    ServletUtils.renderString(response, JSONObject.toJSONString(ajaxResult));
                    return false;
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request
     * @return
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request);
}

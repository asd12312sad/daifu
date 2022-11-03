package com.ms.common.utils;

import com.ms.common.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取对象的IP地址等信息
 *
 * @author xiao
 * @date 2020.08.17
 */
public class CusAccessObjectUtil {


    /**
     * 默认IP地址
     */
    public final static String ERROR_IP = "127.0.0.1";

    /**
     * IP地址正则表达式
     */
    public final static Pattern pattern = Pattern.compile(
            "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})");

    /**
     * @param request
     * @return boolean
     * @author xiao
     * @title isValidIP
     * <ul>
     * @description 根据请求验证IP地址
     * </ul>
     * @createdTime 2020.08017
     * @version : V1.0.0
     */
    public static boolean isValidIP(HttpServletRequest request) {
        // 获取用户真实的IP地址
        String ip = getUserIP(request);
        // 验证IP地址是否符合规则
        return isValidIP(ip);
    }

    /**
     * @param request
     * @return String
     * @author xiao
     * @title getRemoteIp
     * <ul>
     * @description 获取远程IP地址
     * </ul>
     * @createdTime 2020.08.17
     * @version : V1.0.0
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-real-ip");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        // 过滤反向代理的ip
        String[] stemps = ip.split(",");
        if (stemps != null && stemps.length >= 1) {
            // 得到第一个IP，即客户端真实IP
            ip = stemps[0];
        }

        ip = ip.trim();
        if (ip.length() > 23) {
            ip = ip.substring(0, 23);
        }

        return ip;
    }

    /**
     * @param request
     * @return String
     * @author xiao
     * @title getUserIP
     * <ul>
     * @description 获取用户真实的IP地址
     * </ul>
     * @createdTime 2020.08.17
     * @version : V1.0.0
     */
    public static String getUserIP(HttpServletRequest request) {
        // 优先取 X-Real-IP
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = ERROR_IP;
            }
        }
        if ("unknown".equalsIgnoreCase(ip)) {
            ip = ERROR_IP;
            return ip;
        }
        int index = ip.indexOf(',');
        if (index >= 0) {
            ip = ip.substring(0, index);
        }

        return ip;
    }

    /**
     * @param request
     * @return String
     * @author xiao
     * @title getLastIpSegment
     * <ul>
     * @description 获取末尾IP地址段，{@linkplain IP地址简介}
     * </ul>
     * @createdTime 2020.08.17
     * @version : V1.0.0
     */
    public static String getLastIpSegment(HttpServletRequest request) {
        // 获取用户真实的IP地址
        String ip = getUserIP(request);
        if (ip != null) {
            ip = ip.substring(ip.lastIndexOf('.') + 1);
        } else {
            ip = "0";
        }
        return ip;
    }

    /**
     * @param ip
     * @return boolean
     * @author xiao
     * @title isValidIP
     * <ul>
     * @description 验证IP地址是否符合规则
     * </ul>
     * @createdTime 2020.08.17
     * @version : V1.0.0
     */
    public static boolean isValidIP(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        Matcher matcher = pattern.matcher(ip);
        boolean isValid = matcher.matches();
        return isValid;
    }

    /**
     * @return String
     * @author xiao
     * @title getLastServerIpSegment
     * <ul>
     * @description 获取服务器的会后一个地址段
     * </ul>
     * @createdTime 2020.08.17
     * @version : V1.0.0
     */
    public static String getLastServerIpSegment() {
        String ip = getServerIP();
        if (ip != null) {
            ip = ip.substring(ip.lastIndexOf('.') + 1);
        } else {
            ip = "0";
        }
        return ip;
    }

    /**
     * @return String
     * @author xiao
     * @title getServerIP
     * <ul>
     * @description 获取服务器IP地址
     * </ul>
     * @createdTime 2020.08.17
     * @version : V1.0.0
     */
    public static String getServerIP() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            String hostAddress = inetAddress.getHostAddress();
            return hostAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ERROR_IP;
    }

    /**
     * @param request
     * @return String
     * @author xiao
     * @title getIpAddress
     * <ul>
     * @description              <li>获取IP地址
     * <li><strong>注意：</strong>IP地址经过多次反向代理后会有多个IP值，
     * <li>其中第一个IP才是真实IP，所以不能通过request.getRemoteAddr()获取IP地址，
     * <li>如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * <li>X-Forwarded-For中第一个非unknown的有效IP才是用户真实IP地址。
     * </ul>
     * @createdTime 2020.08.18
     * @version : V1.0.0
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        try {
            // 获取用户真是的地址
            String Xip = request.getHeader("X-Real-IP");
            // 获取多次代理后的IP字符串值
            String XFor = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
                // 多次反向代理后会有多个IP值，第一个用户真实的IP地址
                int index = XFor.indexOf(",");
                if (index >= 0) {
                    return XFor.substring(0, index);
                } else {
                    return XFor;
                }
            }
            ip = Xip;
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }
}

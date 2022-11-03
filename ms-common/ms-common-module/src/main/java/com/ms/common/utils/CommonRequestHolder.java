package com.ms.common.utils;



import com.ms.common.core.enums.UserType;
import com.ms.common.core.exception.BaseException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 通用参数
 *
 * @author xiaobing
 * @version v1.0.0
 * @date 2020-03-16 16:04
 * @Description Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2020-03-16 16:04     xiaobing          v1.0.0           Created
 */
public class CommonRequestHolder {


    /**
     * 当前线程是否进行过初始化
     */
    public static final ThreadLocal<Boolean> isInit = new ThreadLocal<>();

    /**
     * 记录当前线程操作用户主键 ID
     */
    public static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();

    /**
     * 记录当前线程操作用户名称
     */
    public static final ThreadLocal<String> currentUserName = new ThreadLocal<>();


    /**
     * 记录当前线程操作用户主键 ID
     */
    public static final ThreadLocal<Integer> currentUserParentId = new ThreadLocal<>();

    /**
     * 记录当前线程操作用户名称
     */
    public static final ThreadLocal<String> currentUserParentName = new ThreadLocal<>();



    /**
     * 记录当前线程的统一时间错
     */
    public static final ThreadLocal<Date> currentTimestamp = new ThreadLocal<>();

    public static final ThreadLocal<String> tokenmp = new ThreadLocal<>();

    /**
     * 初始化数据，
     * @param userId
     * @param username
     */
    public static void  init(Integer userId, String username) {
        currentUserId.set(userId);
        currentUserName.set(username);
        currentTimestamp.set(new Date());

        isInit.set(true);
    }

    /**
     * 初始化数据，
     * @param userId
     * @param username
     */
    public static void  init(Integer userId, String username,Integer parentId,String parentName) {
        currentUserId.set(userId);
        currentUserName.set(username);
        currentUserParentId.set(parentId);
        currentUserParentName.set(parentName);
        currentTimestamp.set(new Date());

        isInit.set(true);
    }


    public static String getTokenmp() {

        String token = tokenmp.get();

        return token;
    }

    public static Boolean ifInit() {
        Boolean bool =isInit.get();
        return bool;
    }
    public static void isInit() {
        Boolean bool =isInit.get();

        if (bool == null || !bool) {
            throw new BaseException("当前线程基础数据未进行初始化");
        }
    }

    /**
     * 获取当前用户ID
     * @return
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = ServletUtils.getRequest();
        String header = request.getHeader("Authorization");


        String tokenType = request.getHeader("TokenType");

        JWTUtil.JWTResult jwtResult   = JWTUtil.getInstance().checkToken(header.replace("Bearer ", ""), tokenType);
        if (jwtResult.isStatus()) {
            String userId = jwtResult.getUserId();
        return Long.parseLong(userId);    }
        throw new BaseException("当前线程基础数据未进行初始化");

    }
    public static Integer getCurrentDetailUserId() {
        HttpServletRequest request = ServletUtils.getRequest();
        String header = request.getHeader("Authorization");


        String tokenType = request.getHeader("TokenType");

        JWTUtil.JWTResult jwtResult   = JWTUtil.getInstance().checkToken(header.replace("Bearer ", ""), tokenType);
        if (jwtResult.isStatus()) {
            System.out.println("jwtResult.getUserName()"+jwtResult.getUserName());
            System.out.println("jwtResult.getUserId()"+jwtResult.getUserId());
            System.out.println("jwtResult.getPhone()"+jwtResult.getPhone());

            String userId = jwtResult.getUserName();
            return Integer.parseInt(userId);    }
        throw new BaseException("当前线程基础数据未进行初始化");

    }


    /**
     * 获取当前统一时间戳
     * @return
     */
    public static Date getCurrentLocalDateTime() {

        Date now = currentTimestamp.get();

        isInit();

        return now;
    }

    /**
     * 获取当前登陆用户名称
     * @return
     */
    public static String getCurrentUserName() {





        String userName = currentUserName.get();

        isInit();

        return userName;
    }

    /**
     * 获取当前登陆用户名称
     * @return
     */
    public static String getCurrentUserParentName() {

        String userName = currentUserParentName.get();

        isInit();

        return userName;
    }

    /**
     * 获取当前登陆用户名称
     * @return
     */
    public static Integer getCurrentUserParentId() {

        Integer userId = currentUserParentId.get();

        isInit();

        return userId;
    }
    /**
     * 关闭
     */
    public static void close() {
        isInit.remove();
        currentUserName.remove();
        currentUserId.remove();
        currentTimestamp.remove();
        tokenmp.remove();
        currentUserParentId.remove();
        currentUserParentName.remove();
    }


}

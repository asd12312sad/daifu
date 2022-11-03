package com.hjh.web.controller.system;

import java.util.HashMap;
import java.util.Map;

import com.hjh.common.core.domain.AjaxResult;
import com.hjh.framework.web.service.SysLoginService;
import com.hjh.system.service.ISysUserService;
import com.hjh.common.utils.WeChatHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/27 11:49
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/27 11:49        肖兵           v1.0.0           Created
 */
public class WeChatLoginController {

    @Autowired
    private ISysUserService userService;


}

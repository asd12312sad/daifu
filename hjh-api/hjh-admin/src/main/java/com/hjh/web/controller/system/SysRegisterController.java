package com.hjh.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.hjh.common.core.controller.BaseController;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.core.domain.model.RegisterBody;
import com.hjh.framework.web.service.SysRegisterService;
import com.hjh.system.service.ISysConfigService;

/**
 * 注册验证
 *
 * @author hjh
 */
@RestController
public class SysRegisterController extends BaseController {
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user) {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}

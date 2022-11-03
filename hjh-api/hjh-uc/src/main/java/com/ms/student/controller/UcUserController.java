package com.ms.student.controller;

import com.ms.common.controller.BaseController;
import com.ms.common.domain.R;
import com.ms.common.core.domain.ResultCode;
import com.ms.common.core.enums.SmsTemplateEnum;
import com.ms.common.core.utils.StringUtils;
import com.ms.common.domain.*;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.common.utils.SmsUtil;
import com.ms.student.domain.UcUser;
import com.ms.student.domain.dto.LoginDto;
import com.ms.student.domain.dto.RegisterDto;
import com.ms.student.domain.vo.LoginVo;
import com.ms.student.service.UcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息推送
 ¨
 * @author 肖兵
 * @version v1.0.0
 * @date 2020/7/24
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2020/7/24              肖兵             v1.0.0           Created
 */
@RestController
@RequestMapping("/student")
public class UcUserController extends BaseController {

    @Autowired
    private UcUserService userService;
    @Autowired
    private SmsUtil smsUtil;

    /**
     * 总部登录
     */
    @PostMapping("/address/login")
    public R<LoginVo> addressLogin(@RequestParam String address) {
        return userService.addressLogin(address);
    }
    /**
     * 总部登录
     */
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }


    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<UcUser> register(@RequestBody @Valid RegisterDto registerDto) {
        return userService.register(registerDto);
    }
    /**
     * 用户修改头像
     */
    @PostMapping("/update/portrait")
    public R updatePortrait(@RequestParam String portrait,  @RequestParam(required = false) Integer type) {

        return toAjax(userService.updatePortrait(portrait, type));
    }






    /**
     * 用户基本信息
     */
    @GetMapping("/info")
    public R<UcUser> info() {


        UcUser user = userService.getById(CommonRequestHolder.getCurrentUserId());

        if (StringUtils.isNull(user)) {
            return R.failed("登录信息失效");
        }
        if (user.getAvatar()==null){
            user.setAvatar("https://s1.ax1x.com/2022/04/14/LQtZ8K.jpg");
        }
        user.setPassword(null);
        return R.success(user);
    }

    /**
     * 修改密码
     */
    @PostMapping("update/password")
    @ResponseBody
    public R updatePassword(@RequestBody UpdatePassWordDto updatePassWordDto) {
        //手机号格式验证
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9])|(16[6]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(updatePassWordDto.getAccount());
        boolean isMatch = m.matches();
        if (!isMatch) {
            return R.failed(400, "手机号格式不正确");
        }
        return userService.updatePassword(updatePassWordDto);

    }


    /**
     * 获取登录验证码
     */
    @GetMapping("update/code")
    @ResponseBody
    public R updateCode(@RequestParam(value = "account") String account) {
        //手机号格式验证
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9])|(16[6]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(account);
        boolean isMatch = m.matches();
        if (!isMatch) {

            return R.failed(400, "手机号格式不正确");
        }
        Long userCount = userService.selectCount(account);

        if (userCount == 0) {
            return R.setToCode(ResultCode.REGISTRY_NOTHAS);
        }
        return smsUtil.saveCodeToRedis(account, SmsTemplateEnum.FORGET_PASSWORD.getType());

    }

    /**
     * 获取登录验证码
     */
    @GetMapping("login/code")
    @ResponseBody
    public R registryCode(@RequestParam(value = "account") String account) {
        //手机号格式验证
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9])|(16[6]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(account);
        boolean isMatch = m.matches();
        if (!isMatch) {

            return R.failed(400, "手机号格式不正确");
        }
        Long userCount = userService.selectCount(account);

        if (userCount == 0) {
            return R.setToCode(ResultCode.REGISTRY_NOTHAS);
        }
        return smsUtil.saveCodeToRedis(account, SmsTemplateEnum.LOG_IN.getType());

    }
}

package com.ms.agent.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.agent.domain.Agent;
import com.ms.agent.pojo.vo.SysSettingVO;
import com.ms.agent.service.AgentService;
import com.ms.agent.pojo.vo.LoginVo;
import com.ms.agent.service.IMerchantPayOrderService;
import com.ms.agent.util.GoogleAuthenticator;
import com.ms.common.core.domain.LoginUserVo;
import com.ms.common.domain.R;
import com.ms.common.core.enums.UserType;
import com.ms.common.core.utils.GsonUtil;
import com.ms.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "sso")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping("/login")
    public R login(@RequestParam String email, @RequestParam String password, @RequestParam String googleSign) {
        Agent agent = agentService.getOne(new QueryWrapper<Agent>().eq("account", email));
        if (agent == null) {
            return R.failed(401, "账号不存在");
        }
        boolean flag = SecurityUtils.matchesPassword(password, agent.getPassword());
        if (!flag) {
            return R.failed(401, "密码错误");
        }

        Boolean authcode = GoogleAuthenticator.authcode(googleSign, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }

        // 登录token
        long timeMillis = System.currentTimeMillis();
        String token = JWTUtil.getInstance().generateToken(agent.getId() + "", agent.getName(), agent.getAccount(), 604800, UserType.ADMIN.getCode(), timeMillis);

        //封装redis对象
        LoginUserVo redisUserVo = new LoginUserVo();
        redisUserVo.setName(agent.getName());
        redisUserVo.setId(agent.getId().intValue());
        redisUserVo.setParentId(agent.getId().intValue());
        redisUserVo.setParentName(agent.getName());
        redisUserVo.setLoginTime(timeMillis);


        System.out.println(agent.getId() + agent.getAccount() + agent.getName() + UserType.ADMIN.getCode());
        redisTemplate.opsForValue().set(agent.getId() +agent.getName() +  agent.getAccount() + UserType.ADMIN.getCode(), GsonUtil.getJson(redisUserVo));
        String newToken = "Bearer " + token;
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(newToken);
        loginVo.setTokenType(UserType.ADMIN.getCode());
        agent.setLastLoginDate(new Date());
        if (agent.getLoginCount() == null) {
            agent.setLoginCount(0);
        }
        agent.setLoginCount(agent.getLoginCount() + 1);
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        agent.setIp(ip);
        this.agentService.updateById(agent);
        return R.success(loginVo);
    }


    @GetMapping("/getGoogleSign")
    public R getGoogleSign(String email, String password) {


        Agent agent = agentService.getOne(new QueryWrapper<Agent>().eq("account", email));
        if (agent == null) {
            return R.failed(401, "账号不存在");

        }

        boolean flag = SecurityUtils.matchesPassword(password, agent.getPassword());
        if (!flag) {
            return R.failed(401, "密码错误");
        }
        if (agent.getLastLoginDate() != null) {
            return R.failed(898, "不是第一次登录");
        }
        if (agent.getGoogleSignCode() == null) {
            agent.setGoogleSignCode(GoogleAuthenticator.generateSecretKey());
            agentService.updateById(agent);
        }
        String host = "otpauth://totp/" + agent.getAccount() + "@host?secret=" + agent.getGoogleSignCode();
        Map<String, String> map = new HashMap<>();
        map.put("sign", host);
        return R.success(map);
    }

    public static void main(String[] args) {
        String host = "otpauth://totp/" + "heng888" + "@host?secret=" + "ETDTY2GXSGTRQEPU";
        System.out.println(host);

    }
    @GetMapping("/resetSign")
    public R resetSign(String googleSign) {


        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        if (agent == null) {
            return R.failed(401, "账号不存在");

        }
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }
        agent.setGoogleSignCode(GoogleAuthenticator.generateSecretKey());
        agentService.updateById(agent);
        String host = "otpauth://totp/" + agent.getAccount() + "@host?secret=" + agent.getGoogleSignCode();
        Map<String, String> map = new HashMap<>();
        map.put("sign", host);
        return R.success(map);
    }


    @GetMapping("/info")
    public R info() {

        Long userId = CommonRequestHolder.getCurrentUserId();
        SysSettingVO sysSettingVO = merchantPayOrderService.financeInfo();

        Agent agent = agentService.getById(userId);
        agent.setUnbalancedAmount(sysSettingVO.getOutstandingAmount());
        agent.setGoogleSignCode(null);
        return R.success(agent);
    }


    @GetMapping("/reset/password")
    public R resetPassword(@RequestParam String password, String googleSign) {
        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        if (agent == null) {
            return R.failed(401, "账号不存在");
        }
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "谷歌验证码错误");
        }

        agent.setPassword(SecurityUtils.encryptPassword(password));
        this.agentService.updateById(agent);
        return R.success();
    }


}

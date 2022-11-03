package com.ms.agent.controller;

import com.ms.agent.domain.Agent;
import com.ms.agent.pojo.vo.HomeCountVO;
import com.ms.agent.service.AgentService;
import com.ms.agent.service.IMerchantPayOrderService;
import com.ms.agent.service.MerchantService;
import com.ms.agent.util.GoogleAuthenticator;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("home")
public class HomeController {
    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AgentService agentService;

    @GetMapping("/sum")
    public R index() {
        HomeCountVO homeCountVO = merchantPayOrderService.selectHomeCount();
        return R.success(homeCountVO);
    }

    @GetMapping("/collection/header")
    public R collectionHeader() {
        return R.success(merchantPayOrderService.collectionHeader());
    }

    @GetMapping("/pay/header")
    public R payHeader() {
        return R.success(merchantPayOrderService.payHeader());
    }

    @GetMapping("/week/sum")
    public R weekSum() {
        Map<String, Object> map = merchantPayOrderService.weekSum();
        return R.success(map);
    }

    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "test")
    public R test(String googleSign,Long merchantId) {


        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        return R.success(merchantService.test(merchantId));
    }

    /**
     * 获取商户管理详细信息
     */
    @GetMapping(value = "testPay")
    public R testPay(@RequestParam String address, @RequestParam BigDecimal amount, String googleSign,Long merchantId) {
        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        return R.success(merchantService.testPay(address, amount,merchantId));
    }
}

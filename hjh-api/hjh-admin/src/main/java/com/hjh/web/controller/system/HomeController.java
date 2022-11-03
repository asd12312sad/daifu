package com.hjh.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.erp.domain.HomeCountVO;
import com.hjh.erp.mapper.OrderMapper;
import com.hjh.erp.mapper.UcUserMapper;
import com.hjh.erp.service.IMerchantPayAddressService;
import com.hjh.erp.service.IMerchantPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;

    @GetMapping("/sum")
    public AjaxResult index(){
        HomeCountVO homeCountVO =  merchantPayOrderService.selectHomeCount();
        return AjaxResult.success(homeCountVO);
    }


    @GetMapping("/week/sum")
    public AjaxResult weekSum(){
        Map<String,Object> map =  merchantPayOrderService.weekSum();
        return AjaxResult.success(map);
    }
}

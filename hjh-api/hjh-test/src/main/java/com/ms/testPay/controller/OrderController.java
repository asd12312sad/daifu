package com.ms.testPay.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.domain.R;
import com.ms.testPay.domain.OmsOrder;
import com.ms.testPay.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderMapper orderMapper;


    @RequestMapping("GenerateAddress")
    public R generateAddress() {

        //调用wallet-cli 生成钱包
        return null;


    }


    @RequestMapping("/return/collection")
    public String returnCollection(String amount,String sign,String ownerAddress,String orderNo) {
        String md5 = SecureUtil.md5("8424aece64d2b9dc015ea1c11378787a" + amount + ownerAddress);
        if (!md5.equals(sign)){
           return "验签失败";
        }
        OmsOrder order = orderMapper.selectOne(new QueryWrapper<OmsOrder>().eq("order_sn", orderNo));
        if (order == null){
            return "订单不存在";

        }

        order.setStatus(2);
        order.setOwnerAddress(ownerAddress);
        orderMapper.updateById(order);
        return "success";
    }


    @RequestMapping("/return/pay")
    public String returnPay(String amount,String sign,String ownerAddress,String orderNo) {

        String md5 = SecureUtil.md5("8424aece64d2b9dc015ea1c11378787a" + amount+ownerAddress);
        if (!md5.equals(sign)) {
         return "ERROR";
        }
        OmsOrder order = orderMapper.selectOne(new QueryWrapper<OmsOrder>().eq("order_sn", orderNo));
        if (order==null){
            return "ERROR";
        }
        order.setStatus(2);
        order.setOwnerAddress(ownerAddress);
        order.setRealAmount(new BigDecimal(amount));
        orderMapper.updateById(order);
        return "SUCCESS";
    }

    @RequestMapping("/testCollection")
    public R testCollection(BigDecimal amount) {
        OmsOrder order = new OmsOrder();
        order.setOrderSn("test"+System.currentTimeMillis());
        order.setStatus(0);
        order.setAmount(amount);
        order.setOrderType("1");
        orderMapper.insert(order);
        String md5 = SecureUtil.md5("8424aece64d2b9dc015ea1c11378787a" + order.getOrderSn());
        String returnUrl = "http://47.242.72.36:10086/order/return/collection";
        String json = HttpUtil.post("https://api.adminuu.pro/ucc/pay/createAddress?merchantId="+45+"&sign="+md5+"&orderNo="+order.getOrderSn()+"&amount="+amount+"&returnAddress="+returnUrl,"{}");
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            order.setStatus(3);
            order.setResult(msg);
            orderMapper.updateById(order);

            return R.failed(msg);
        }else {
            String address = JSON.parseObject(json).getJSONObject("data").getString("address");
            order.setAddress(address);
            order.setStatus(1);
            order.setResult("SUCCESS");
            orderMapper.updateById(order);
            return R.success(jsonObject.getJSONObject("data"));

        }

    }
}

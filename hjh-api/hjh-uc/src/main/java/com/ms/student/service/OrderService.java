package com.ms.student.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ms.student.domain.*;

public interface OrderService  extends IService<Order> {


    void test(int i);

    void inform(String transferId, MerchantPayAddress merchantPayAddress);


    void rechargePay(JSONObject jsonObject, MerchantPayOrder merchantPayAddress);

    void rechargeRecharge(String transactionId, Merchant merchant);

    void replenishment(MerchantPayAddress merchantPayAddress);

    void foGather(MerchantCollectUseAddress merchantCollectUseAddress,Merchant merchant);

}

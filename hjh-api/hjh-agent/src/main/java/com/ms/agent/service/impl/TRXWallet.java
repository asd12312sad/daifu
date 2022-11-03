package com.ms.agent.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.ms.agent.domain.MerchantPayAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;

@Slf4j
@Component
public class TRXWallet  {

    @Autowired
    private RestTemplate restTemplate;



    /**
     * TRX主网
     */
    private static final String tronUrl = "https://api.trongrid.io";

    private static final String WALLET_URL = "https://api.trongrid.io";

    private static final long feeLimit = 1000000L;

    /**
     * 代币精度
     */
    //token的精度  就是小数点后面有多少位小数 然后1后面加多少个0就可以
    private static final BigDecimal decimal = new BigDecimal("1000000");
//
//    @Autowired
//    private TronFullNodeFeign feign;
public static JSONObject createAddress(){

    String s = HttpUtil.get("http://172.28.27.53:8659/mer/getTranAddress");
    JSONObject jsonObject = JSON.parseObject(s);
    return jsonObject;

}

    public  BigDecimal usdtBalanceOf(String address) {
        String s = HttpUtil.get("https://apilist.tronscanapi.com/api/account/token_asset_overview?address="+address);


        JSONObject jsonObject;

        try{
            jsonObject  = JSON.parseObject(s);
        }catch (Exception e){
            e.printStackTrace();
            log.error("地址获取余额失败"+address);
            return BigDecimal.ZERO;

        }
        JSONArray data = jsonObject.getJSONArray("data");
        if (data==null){
            return BigDecimal.ZERO;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            if (jsonObject1.getString("tokenId")!=null && jsonObject1.getString("tokenId").equals("TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t")){
                return new BigDecimal(jsonObject1.getString("balance")).divide(decimal, 6, RoundingMode.DOWN);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 查询tron币数量
     *
     * @param address
     * @return
     */
    public BigDecimal balanceOfTron(String address) {
        final BigDecimal decimal = new BigDecimal("1000000");
        final int accuracy = 6;//六位小数
        String s=    HttpUtil.get("https://api.trongrid.io/wallet/getaccount?address="+address+"&visible=true");
        JSONObject obj =JSONObject.parseObject(s);
        if (obj != null) {
            BigInteger balance = obj.getBigInteger("balance");
            if (balance == null) balance = BigInteger.ZERO;
            return new BigDecimal(balance).divide(decimal, accuracy, RoundingMode.DOWN);
        }
        return BigDecimal.ZERO;
    }
















}

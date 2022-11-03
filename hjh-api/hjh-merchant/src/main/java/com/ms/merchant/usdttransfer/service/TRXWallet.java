package com.ms.merchant.usdttransfer.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ms.merchant.domain.Merchant;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.MerchantPayUseAddress;
import com.ms.merchant.mapper.MerchantPayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



@Slf4j
@Component
public class TRXWallet {



    @Autowired
    private MerchantPayOrderMapper merchantPayOrderMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * trc20合约地址 这个是USDT代币
     */
    private String contract = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    private static byte addressPreFixByte = (byte) 0x41; // 41 + address (byte) 0xa0; //a0 + address

    /**
     * TRX主网
     */
    private static final String tronUrl = "https://api.trongrid.io";

    private static final String WALLET_URL = "https://api.trongrid.io";

    private static final long feeLimit = 1000000L;
    final int accuracy = 6;//六位小数

    /**
     * 代币精度
     */
    //token的精度  就是小数点后面有多少位小数 然后1后面加多少个0就可以
    private static final BigDecimal decimal = new BigDecimal("1000000");
//
//    @Autowired
//    private TronFullNodeFeign feign;

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
                    return new BigDecimal(jsonObject1.getString("balance")).divide(decimal, accuracy, RoundingMode.DOWN);
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


    /**
     * 发送签名交易
     *
     * @param privateKey
     * @param toAddress
     * @param amount
     * @return
     */
    public String sendTransaction(String privateKey,
                                  String fromAddress,
                                  String toAddress, BigDecimal amount
            ,String orderNo) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("转账失败:额度不符合规则 " + amount);
            return "401";
        }

        String s = HttpUtil.get("http://172.28.27.53:8659/mer/tranTrx?key=" + privateKey + "&ownerAddress=" + fromAddress + "&toAddress=" + toAddress + "&amount=" + amount + "&orderNo=" + orderNo);
        if (JSON.parseObject(s).getString("status").equals("1")){

            return JSON.parseObject(s).getString("txid");
        }else {
            return "402";
        }
    }





    public String usdtSendTransaction(String fromAddress, String privateKey, BigDecimal amount, String toAddress,String orderNo) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("转账失败:额度不符合规则 " + amount);
            return "401";
        }

        String s = HttpUtil.get("http://172.28.27.53:8659/mer/tranUsdt?key=" + privateKey + "&ownerAddress=" + fromAddress + "&toAddress=" + toAddress + "&amount=" + amount + "&orderNo=" + orderNo);
        if (JSON.parseObject(s).getString("status").equals("1")){

            return JSON.parseObject(s).getString("txid");
        }else {
            return "402";
        }
    }



    public Map<String, String> createPay(Merchant merchant, MerchantPayUseAddress useAddress, MerchantPayOrder merchantPayOrder) {
        Map<String, String> map = new HashMap<>();
        merchantPayOrder.setOwnerAddress(useAddress.getAddress());
        //通过redis来做判断每个商户代付一分钟以内最多发起30笔交易
        redisTemplate.opsForValue().setIfAbsent(merchant.getId() + ":orderNumber", "", 1, TimeUnit.MINUTES);
        String merchantOrderNumber = redisTemplate.opsForValue().get(merchant.getId() + ":orderNumber");

        if (merchantOrderNumber != null && merchantOrderNumber.length() >= 30) {
            merchantPayOrder.setStatus(3);
            map.put("MSG", "订单加入队列等待中");
            map.put("ORDER_NO", "");
            merchantPayOrderMapper.updateById(merchantPayOrder);
            return map;
        }
        redisTemplate.opsForValue().append(merchant.getId() + ":orderNumber", "1");
        String result = usdtSendTransaction(useAddress.getAddress(), useAddress.getPrivateKey(), merchantPayOrder.getUsdtBalance(), merchantPayOrder.getPayAddress(),merchantPayOrder.getOrderSn());
        if ("401".equals(result)) {
            System.out.println("转账失败:额度不符合规则");
            merchantPayOrder.setPayResult("转账失败:额度不符合规则");
            merchantPayOrder.setStatus(0);
            map.put("MSG", "转账失败:额度不符合规则");
            map.put("ORDER_NO", "");

        } else if ("402".equals(result)) {
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setPayResult("创建交易失败");
            map.put("MSG", "创建交易失败");
            map.put("ORDER_NO", "");
        } else {
            merchantPayOrder.setTxid(result);
            merchantPayOrder.setStatus(1);
            map.put("MSG", "SUCCESS");
            map.put("ORDER_NO", "");
        }

        merchantPayOrderMapper.updateById(merchantPayOrder);
        return map;
    }
    /**
     * 具体方法
     */
    public  static  JSONObject createAddress() {

        String s = HttpUtil.get("http://172.28.27.53:8659/mer/getTranAddress");
        return JSON.parseObject(s);
    }


    public boolean lockPayOrder( MerchantPayOrder merchantPayOrder) {
        //延时三秒获取订单锁 同一订单强制单线程处理
        for (int i = 0; i < 50; i++) {
            try {
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lockPayOrderAudit:lock:" + merchantPayOrder.getId(), "",10, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(ifAbsent)) {
                    return true;
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    public void unLockPayOrder( Long orderId) {
        redisTemplate.delete("lockPayOrderAudit:lock:" + orderId);
    }


}

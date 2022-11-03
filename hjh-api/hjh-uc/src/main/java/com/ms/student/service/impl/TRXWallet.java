package com.ms.student.service.impl;
import java.util.Date;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.utils.OrderUtil;
import com.ms.student.domain.*;
import com.ms.student.mapper.MerchantCollectUseAddressMapper;
import com.ms.student.mapper.MerchantPayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class TRXWallet  {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TRXWallet trxWallet;


    @Autowired
    private MerchantPayOrderMapper merchantPayOrderMapper;

    @Autowired
    private MerchantCollectUseAddressMapper merchantCollectUseAddressMapper;




    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static SecureRandom random = new SecureRandom();


    private static final Integer PRECIS = 18;

    /**
     * trc20合约地址 这个是USDT代币
     */
    private String contract = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";


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

    public  BigDecimal usdtBalanceOf(String address) {
        try {
            return     usdtBalanceOf2(address);
        }catch (Exception e){
            try {
                return     usdtBalanceOf2(address);
            }catch (Exception e1){
                return BigDecimal.ZERO;
            }
        }
    }


    public  BigDecimal usdtBalanceOf2(String address) {
        String s = HttpUtil.get("https://apilist.tronscanapi.com/api/account/tokens?address="+address+"&start=0&limit=20&token=Tether%20USD&hidden=0&show=0&sortType=0");


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


    public String getTransactionById(String txId) {
        Map<String, Object> param = new HashMap<>();
        param.put("value", txId);
        String url = tronUrl + "/wallet/gettransactioninfobyid";
        String jsonStr = restTemplate.postForEntity(url, param, String.class).getBody();
        return jsonStr;
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


    public String usdtSendTransaction(String fromAddress, String privateKey, BigDecimal amount, String toAddress,String orderNo) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("转账失败:额度不符合规则 " + amount);
            return "401";
        }

        String s = HttpUtil.get("http://172.28.27.53:8659/mer/tranUsdt?key=" + privateKey + "&ownerAddress=" + fromAddress + "&toAddress=" + toAddress + "&amount=" + amount + "&orderNo=" + orderNo);
        if (JSON.parseObject(s).getString("status").equals("1")){

            return JSON.parseObject(s).getString("txid");
        }else {
            String txid = getUsdtSendFlag(toAddress, orderNo);
            if (!"401".equals(txid)){
                return txid;
            }
            txid = getUsdtSendFlag(toAddress, orderNo);
            if (!"401".equals(txid)){
                return txid;
            }
            s=  HttpUtil.get("http://172.28.27.53:8659/mer/tranUsdt?key=" + privateKey + "&ownerAddress=" + fromAddress + "&toAddress=" + toAddress + "&amount=" + amount + "&orderNo=" + orderNo);
            if (JSON.parseObject(s).getString("status").equals("1")){
                return JSON.parseObject(s).getString("txid");
            }else {
                txid = getUsdtSendFlag(toAddress, orderNo);
                if (!"401".equals(txid)){
                    return txid;
                }else{
                    return "402";
                }

            }
        }
    }



    private String getUsdtSendFlag(String toAddress,String orderNo){

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String json = HttpUtil.get("https://api.trongrid.io/v1/accounts/" +toAddress + "/transactions/trc20?only_to=true&limit=50&contract_address=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                try {
                    String transactionId = data.getJSONObject(i).getString("transaction_id");
                    json = HttpUtil.get("https://apilist.tronscan.org/api/transaction-info?hash=" + transactionId);
                    JSONObject transactionInfoJSONObject = JSONObject.parseObject(json);
                    String memo = transactionInfoJSONObject.getString("data");
                    if (orderNo.equals(memo)) {
                        return transactionId;
                    }
                }catch (Exception e){
                }
            }
            return "401";

    }
    /**
     * 补充0到64个字节
     *
     * @param dt
     * @return
     */
    private String addZero(String dt, int length) {
        StringBuilder builder = new StringBuilder();
        final int count = length;
        int zeroAmount = count - dt.length();
        for (int i = 0; i < zeroAmount; i++) {
            builder.append("0");
        }
        builder.append(dt);
        return builder.toString();
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
        System.out.println("http://172.28.27.53:8659/mer/tranTrx?key=" + privateKey + "&ownerAddress=" + fromAddress + "&toAddress=" + toAddress + "&amount=" + amount + "&orderNo=" + orderNo);
        System.out.println("sendTransaction"+ s);
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
        String result = trxWallet.usdtSendTransaction(useAddress.getAddress(), useAddress.getPrivateKey(), merchantPayOrder.getUsdtBalance(), merchantPayOrder.getPayAddress(),merchantPayOrder.getOrderSn());
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

    public Map<String, String> createPayByQueue(Merchant merchant, MerchantPayUseAddress useAddress, MerchantPayOrder merchantPayOrder) {
        Map<String, String> map = new HashMap<>();

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

        String result = trxWallet.usdtSendTransaction(useAddress.getAddress(), useAddress.getPrivateKey(), merchantPayOrder.getUsdtBalance(), merchant.getUsdtAddress(),merchantPayOrder.getOrderSn());
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

    public MerchantPayAddress getAddress(Long merchantId) {
        MerchantPayAddress merchantPayAddress =  new  MerchantPayAddress();
        merchantPayAddress.setMerchantId(merchantId.toString());
        merchantPayAddress.setOrderSn(merchantId + OrderUtil.orderSn() + OrderUtil.createShareCode());
        merchantPayAddress.setGather(0);
        merchantPayAddress.setCreateTime(new Date());
        merchantPayAddress.setHaveUsdt(0);
        merchantPayAddress.setStatus(0);
        merchantPayAddress.setUsdtBalance(BigDecimal.ZERO);
        merchantPayAddress.setTrxBalance(BigDecimal.ZERO);
        merchantPayAddress.setFeeAmount(new BigDecimal("0"));
        merchantPayAddress.setPayResult("");
        merchantPayAddress.setMessage("");
        MerchantCollectUseAddress useAddress = this.getUseAddress(merchantId, merchantPayAddress.getOrderSn());
        merchantPayAddress.setAddress(useAddress.getAddress());
        merchantPayAddress.setPrivateKey(useAddress.getPrivateKey());
        merchantPayAddress.setPrivateKeyBase(useAddress.getPrivateKeyBase());
        merchantPayAddress.setHexAddress(useAddress.getHexAddress());
        return merchantPayAddress;
    }

    public MerchantCollectUseAddress getUseAddress(Long merchantId,String orderSn){

        lockGetAddress(merchantId);
        Long count = merchantCollectUseAddressMapper.selectCount(new QueryWrapper<MerchantCollectUseAddress>().eq("merchant_id", merchantId));
        if (count==0){
            for (int i = 0; i < 20; i++) {
                genderCollectAddress(merchantId);
            }
        }
        MerchantCollectUseAddress merchantCollectUseAddress = merchantCollectUseAddressMapper.selectOne(new QueryWrapper<MerchantCollectUseAddress>().eq("merchant_id", merchantId).isNull("order_sn").eq("status",0).orderByDesc("amount").last(" limit 1"));
        if (merchantCollectUseAddress == null){
            merchantCollectUseAddress =  this.genderCollectAddress(merchantId);
        }
        merchantCollectUseAddress.setOrderSn(orderSn);
        merchantCollectUseAddressMapper.updateById(merchantCollectUseAddress);
        unLockGetAddress(merchantId);
        return merchantCollectUseAddress;
    }

    private MerchantCollectUseAddress genderCollectAddress(Long merchantId) {

        String s = HttpUtil.get("http://172.28.27.53:8659/mer/getTranAddress");
        JSONObject jsonObject = JSON.parseObject(s);
        MerchantCollectUseAddress merchantCollectUseAddress = new MerchantCollectUseAddress();
        merchantCollectUseAddress.setMerchantId(merchantId);
        merchantCollectUseAddress.setAddress(jsonObject.getString("address"));
        merchantCollectUseAddress.setPrivateKey(jsonObject.getString("apiKey"));
        merchantCollectUseAddress.setPrivateKeyBase(jsonObject.getString("apiKey"));
        merchantCollectUseAddress.setHexAddress(jsonObject.getString("apiKey"));
        merchantCollectUseAddress.setCreateBy("");
        merchantCollectUseAddress.setStatus(0);
        merchantCollectUseAddress.setCreateTime(new Date());
        merchantCollectUseAddress.setUpdateBy("");
        merchantCollectUseAddress.setUpdateTime(new Date());
        merchantCollectUseAddress.setRemark("");
        merchantCollectUseAddress.setAmount(new BigDecimal("0"));
        merchantCollectUseAddress.setOrderSn(null);
        merchantCollectUseAddressMapper.insert(merchantCollectUseAddress);
        return merchantCollectUseAddress;


    }


    public void lockGetAddress(Long merchantId) {
        //延时三秒获取订单锁 同一订单强制单线程处理
        for (int i = 0; i < 500; i++) {
            try {
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lockGetAddress:lock:" + merchantId, "",10, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(ifAbsent)) {
                    return;
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void lockAddress(String address) {
        //延时三秒获取订单锁 同一订单强制单线程处理
        for (int i = 0; i < 500; i++) {
            try {
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lockAddress:lock:" + address, "",10, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(ifAbsent)) {
                    return;
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void unLockAddress(String address) {
        //延时三秒获取订单锁 同一订单强制单线程处理
        for (int i = 0; i < 500; i++) {
         Boolean ifAbsent = redisTemplate.delete("lockAddress:lock:" + address);
        }
    }

    public void unLockGetAddress( Long merchantId) {
        redisTemplate.delete("lockGetAddress:lock:" + merchantId);
    }

}

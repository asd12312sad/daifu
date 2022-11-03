package com.hjh.usdttransfer.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class TRXWallet {
    //状态解释
    //https://cn.developers.tron.network/docs/%E4%BA%A4%E6%98%93
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String TRANSFER_TYPE = "TransferContract";
    public static final int HEX_ADDRESS_SIZE = 21;
    public static final byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address


    @Autowired
    private RestTemplate restTemplate;


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
     * 具体方法
     */
    public  static  JSONObject createAddress() {

        String s = HttpUtil.get("http://172.28.27.53:8659/mer/getTranAddress");
        return JSON.parseObject(s);
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






}

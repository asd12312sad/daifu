package com.ms.student.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.domain.R;
import com.ms.common.domain.RequestLog;
import com.ms.common.utils.IpUtils;
import com.ms.common.utils.OrderUtil;
import com.ms.common.utils.ServletUtils;
import com.ms.student.domain.*;
import com.ms.student.domain.dto.CreateAddressDto;
import com.ms.student.domain.dto.CreateOrderDto;
import com.ms.student.mapper.*;
import com.ms.student.service.impl.TRXWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private TRXWallet trxWallet;
    @Autowired
    private RequestLogMapper requestLogMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantPayAddressMapper merchantPayAddressMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MerchantPayOrderMapper merchantPayOrderMapper;

    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;

    @Autowired
    private MerchantPayOrderAuditMapper merchantPayOrderAuditMapper;

    @PostMapping("/createAddress")
    public Object createAddress(@RequestParam String merchantId, @RequestParam BigDecimal amount, @RequestParam String sign, @RequestParam String orderNo, @RequestParam(required = false) String returnAddress, @RequestParam(required = false) String ownerAddress, @RequestParam(required = false) String backUrl,@RequestParam(required = false) String params) {
        RequestLog requestLog = new RequestLog();
        requestLog.setName("??????????????????");
        requestLog.setMerchantId(Long.valueOf(merchantId));
        requestLog.setRequestTime(new Date());
        requestLog.setIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        JSONObject requestJson = new JSONObject();
        requestJson.put("merchantId", merchantId);
        requestJson.put("amount", amount);
        requestJson.put("sign", sign);
        requestJson.put("orderNo", orderNo);
        requestJson.put("returnAddress", returnAddress);
        requestJson.put("ownerAddress", ownerAddress);
        requestLog.setRequestLog(requestJson.toJSONString());
        requestLog.setRequestTime(new Date());
        requestLog.setType(2);
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            R<Object> r = R.failed(400, "MERCHANT_ID_ERROR");
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }
        MerchantPayAddress merchantPayAddress = trxWallet.getAddress(merchant.getId());
        merchantPayAddress.setParams(params);
        merchantPayAddress.setAgentId(merchant.getAgentId());
        merchantPayAddress.setRequestLog(requestJson.toJSONString());
        merchantPayAddress.setReturnAddress(returnAddress == null ? merchant.getReturnAddress() : returnAddress);
        merchantPayAddress.setOrderAmount(amount);
        merchantPayAddress.setBackUrl(backUrl);
        merchantPayAddressMapper.insert(merchantPayAddress);
        boolean ipFlag = false;
        String collectionApiWhile = merchant.getCollectionApiWhile();
        if (collectionApiWhile == null || "".equals(collectionApiWhile)) {
            ipFlag = true;
            collectionApiWhile = "127.0.0.1";
        }
        String[] apiWhileSplit = collectionApiWhile.split(",");

        //??????????????????IP
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        if (ip == null) {
            ip = "127.0.0.1";
        }
        String[] split = ip.split(", ");
        for (String s : split) {
            for (String apiwhile : apiWhileSplit) {
                if (s.equals(apiwhile)) {
                    ipFlag = true;
                    break;
                }
            }
        }
        if (!ipFlag) {
            merchantPayAddress.setPayResult("IP_??????????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMerchantOrderNo(orderNo);

            R<Object> r = R.failed(900, "IP_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            merchantPayAddress.setReturnResponseLog(JSONObject.toJSONString(r));
            requestLogMapper.insert(requestLog);
            merchantPayAddressMapper.updateById(merchantPayAddress);

            return r;
        }
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo);
        if (!md5.equals(sign)) {
            merchantPayAddress.setPayResult("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMerchantOrderNo(orderNo);


            R<Object> r = R.failed(401, "SIGN_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            merchantPayAddress.setReturnResponseLog(JSONObject.toJSONString(r));

            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);

            merchantPayAddressMapper.updateById(merchantPayAddress);
            return r;
        }
        if (merchant.getUsdtAddress() == null || "".equals(merchant.getUsdtAddress()) || merchant.getUsdtAddress().length() < 30 || (!merchant.getUsdtAddress().startsWith("T"))) {
            merchantPayAddress.setPayResult("?????????????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMerchantOrderNo(orderNo);

            R<Object> r = R.failed(402, "MERCHANT_USDT_ADDRESS_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            merchantPayAddress.setReturnResponseLog(JSONObject.toJSONString(r));

            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);

            merchantPayAddressMapper.updateById(merchantPayAddress);
            return r;
        }
        MerchantPayAddress merchantPayAddressOne = merchantPayAddressMapper.selectOne(new QueryWrapper<MerchantPayAddress>().eq("merchant_order_no", orderNo).eq("merchant_id", merchantId));
        if (merchantPayAddressOne != null) {
            R<Object> r = R.failed(402, "MERCHANT_HAVE_ORDER");

            merchantPayAddress.setMerchantOrderNo(orderNo + "??????");
            merchantPayAddress.setPayResult("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));

            merchantPayAddressMapper.updateById(merchantPayAddress);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "????????????", "????????????", merchant.getTrxAddress());
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;

        }
        Boolean s = redisTemplate.opsForValue().setIfAbsent(merchantId + ":" + orderNo, orderNo, 1, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(s)) {
            R<Object> r = R.failed(402, "MERCHANT_HAVE_ORDER");

            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "????????????", "????????????", merchant.getTrxAddress());
            merchantPayAddress.setMerchantOrderNo(orderNo + "??????");
            merchantPayAddress.setPayResult("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));

            merchantPayAddressMapper.updateById(merchantPayAddress);


            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }

        merchantPayAddress.setMerchantOrderNo(orderNo);

        BigDecimal fee = amount.multiply(merchant.getFee());
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            R<Object> r =  R.failed(403, "BALANCE_ERROR");

            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "?????????????????????", "???????????????" + fee.toString() + "????????????" + merchant.getFee().toString(), merchantPayAddress.getAddress());
            merchantPayAddress.setPayResult("???????????????????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));
            merchantPayAddressMapper.updateById(merchantPayAddress);
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();

        }

        if (merchant.getBalance() == null || new BigDecimal("100").compareTo(merchant.getBalance()) > 0) {
            sendTelegramMessage(merchant, "?????????????????????", "?????????????????????", merchant.getBalance().toString(), "?????????????????????", "?????????????????????", "???????????????" + merchant.getBalance().toString() + "??????100USDT???????????????");
        }
        BigDecimal TRCbalance = trxWallet.balanceOfTron(merchant.getTrxAddress());

        if (TRCbalance.compareTo(new BigDecimal("100")) < 0) {
            sendTelegramMessage(merchant, merchant.getTrxAddress(), "??????TRX?????????", merchant.getTrxAddress(), TRCbalance.toString(), "??????TRX???????????????", "???????????????" + TRCbalance.toString() + "??????100TRX???????????????");
        }


        String address = merchantPayAddress.getAddress();
        Map map = new HashMap();
        map.put("cashierDesk","https://www.paycloud.pro/py/?time="+System.currentTimeMillis()+"&address="+address+"&orderNo="+orderNo+"&amount="+amount+"&backUrl="+ backUrl);
        map.put("address", address);
        map.put("backUrl",backUrl);
        R<Object> r =   R.success(map);
        merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));
        merchantPayAddressMapper.updateById(merchantPayAddress);

        requestLog.setResponseLog(JSONObject.toJSONString(r));
        requestLog.setStatus(1);
        requestLogMapper.insert(requestLog);
        return r;

    }


    @PostMapping("/createAddressForMerchant")
    public Object createAddressForMerchant(@RequestBody CreateAddressDto createAddressDto) {

        String merchantId = createAddressDto.getMerchantId();
        String ownerAddress = createAddressDto.getOwnerAddress();
        String returnAddress = createAddressDto.getReturnAddress();
        String sign = createAddressDto.getSign();
        String orderNo = createAddressDto.getOrderNo();
        BigDecimal amount = createAddressDto.getAmount();

        RequestLog requestLog = new RequestLog();
        requestLog.setName("??????????????????");
        requestLog.setMerchantId(Long.valueOf(merchantId));
        requestLog.setRequestTime(new Date());
        requestLog.setIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        JSONObject requestJson = new JSONObject();
        requestJson.put("merchantId", merchantId);
        requestJson.put("amount", amount);
        requestJson.put("sign", sign);
        requestJson.put("orderNo", orderNo);
        requestJson.put("returnAddress", returnAddress);
        requestJson.put("ownerAddress", ownerAddress);
        requestLog.setRequestLog(requestJson.toJSONString());
        requestLog.setRequestTime(new Date());
        requestLog.setType(2);
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            R<Object> r = R.failed(400, "MERCHANT_ID_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }
        MerchantPayAddress merchantPayAddress = trxWallet.getAddress(merchant.getId());
        merchantPayAddress.setParams(createAddressDto.getParams());
        merchantPayAddress.setAgentId(merchant.getAgentId());
        merchantPayAddress.setRequestLog(requestJson.toJSONString());
        merchantPayAddress.setBackUrl(createAddressDto.getBackUrl());
        merchantPayAddress.setReturnAddress(returnAddress == null ? merchant.getReturnAddress() : returnAddress);
        merchantPayAddress.setOrderAmount(amount);
        merchantPayAddressMapper.insert(merchantPayAddress);
        boolean ipFlag = false;
        String collectionApiWhile = merchant.getCollectionApiWhile();
        if (collectionApiWhile == null || "".equals(collectionApiWhile)) {
            ipFlag = true;
            collectionApiWhile = "127.0.0.1";
        }
        String[] apiWhileSplit = collectionApiWhile.split(",");

        //??????????????????IP
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        if (ip == null) {
            ip = "127.0.0.1";
        }
        String[] split = ip.split(", ");
        for (String s : split) {
            for (String apiwhile : apiWhileSplit) {
                if (s.equals(apiwhile)) {
                    ipFlag = true;
                    break;
                }
            }
        }
        if (!ipFlag) {
            merchantPayAddress.setPayResult("IP_??????????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMerchantOrderNo(orderNo);

            R<Object> r = R.failed(900, "IP_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            merchantPayAddress.setReturnResponseLog(JSONObject.toJSONString(r));
            requestLogMapper.insert(requestLog);
            merchantPayAddressMapper.updateById(merchantPayAddress);

            return r;
        }
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo);
        if (!md5.equals(sign)) {
            merchantPayAddress.setPayResult("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMerchantOrderNo(orderNo);


            R<Object> r = R.failed(401, "SIGN_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            merchantPayAddress.setReturnResponseLog(JSONObject.toJSONString(r));

            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);

            merchantPayAddressMapper.updateById(merchantPayAddress);
            return r;
        }
        if (merchant.getUsdtAddress() == null || "".equals(merchant.getUsdtAddress()) || merchant.getUsdtAddress().length() < 30 || (!merchant.getUsdtAddress().startsWith("T"))) {
            merchantPayAddress.setPayResult("?????????????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMerchantOrderNo(orderNo);

            R<Object> r = R.failed(402, "MERCHANT_USDT_ADDRESS_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            merchantPayAddress.setReturnResponseLog(JSONObject.toJSONString(r));

            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);

            merchantPayAddressMapper.updateById(merchantPayAddress);
            return r;
        }
        MerchantPayAddress merchantPayAddressOne = merchantPayAddressMapper.selectOne(new QueryWrapper<MerchantPayAddress>().eq("merchant_order_no", orderNo).eq("merchant_id", merchantId));
        if (merchantPayAddressOne != null) {
            R<Object> r = R.failed(402, "MERCHANT_HAVE_ORDER");

            merchantPayAddress.setMerchantOrderNo(orderNo + "??????");
            merchantPayAddress.setPayResult("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));

            merchantPayAddressMapper.updateById(merchantPayAddress);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "????????????", "????????????", merchant.getTrxAddress());
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;

        }
        Boolean s = redisTemplate.opsForValue().setIfAbsent(merchantId + ":" + orderNo, orderNo, 1, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(s)) {
            R<Object> r = R.failed(402, "MERCHANT_HAVE_ORDER");

            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "????????????", "????????????", merchant.getTrxAddress());
            merchantPayAddress.setMerchantOrderNo(orderNo + "??????");
            merchantPayAddress.setPayResult("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));

            merchantPayAddressMapper.updateById(merchantPayAddress);


            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }

        merchantPayAddress.setMerchantOrderNo(orderNo);

        BigDecimal fee = amount.multiply(merchant.getFee());
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            R<Object> r =  R.failed(403, "BALANCE_ERROR");

            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "?????????????????????", "???????????????" + fee.toString() + "????????????" + merchant.getFee().toString(), merchantPayAddress.getAddress());
            merchantPayAddress.setPayResult("???????????????????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));

            merchantPayAddressMapper.updateById(merchantPayAddress);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();

        }

        if (merchant.getBalance() == null || new BigDecimal("100").compareTo(merchant.getBalance()) > 0) {
            sendTelegramMessage(merchant, "?????????????????????", "?????????????????????", merchant.getBalance().toString(), "?????????????????????", "?????????????????????", "???????????????" + merchant.getBalance().toString() + "??????100USDT???????????????");
        }
        BigDecimal TRCbalance = trxWallet.balanceOfTron(merchant.getTrxAddress());
//        if (TRCbalance.compareTo(new BigDecimal("10")) <= 0) {
//            R<Object> r =   R.failed(403, "TRC_BALANCE_ERROR");
//
//
//            merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));
//            merchantPayAddress.setPayResult("??????TRX?????????????????????");
//            merchantPayAddress.setStatus(6);
//            merchantPayAddressMapper.updateById(merchantPayAddress);
//            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "??????TRX???????????????", "???????????????" + BigDecimal.TEN + "????????????" + TRCbalance, merchant.getTrxAddress());
//
//
//
//
//            requestLog.setResponseLog(JSONObject.toJSONString(r));
//            requestLog.setStatus(0);
//            requestLogMapper.insert(requestLog);
//            return r;
//
//        }
        if (TRCbalance.compareTo(new BigDecimal("100")) < 0) {
            sendTelegramMessage(merchant, merchant.getTrxAddress(), "??????TRX?????????", merchant.getTrxAddress(), TRCbalance.toString(), "??????TRX???????????????", "???????????????" + TRCbalance.toString() + "??????100TRX???????????????");
        }


        String address = merchantPayAddress.getAddress();
        Map map = new HashMap();
        map.put("address", address);
        map.put("cashierDesk","https://www.paycloud.pro/py/?time="+System.currentTimeMillis()+"&address="+address+"&orderNo="+orderNo+"&amount="+amount+"&backUrl="+ createAddressDto.getBackUrl());
        map.put("backUrl",createAddressDto.getBackUrl());
        R<Object> r =   R.success(map);

        merchantPayAddress.setResponseLog(JSONObject.toJSONString(r));
        merchantPayAddressMapper.updateById(merchantPayAddress);

        requestLog.setResponseLog(JSONObject.toJSONString(r));
        requestLog.setStatus(1);
        requestLogMapper.insert(requestLog);
        return r;

    }

//
//2.????????????????????????1000U???????????????????????????????????????????????????????????????????????????????????????????????????????????????
//
//3.????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????30????????????????????????????????????????????????????????????????????????


    @RequestMapping("/createPayMerchant")
    public Object createPayMerchant(@RequestBody CreateOrderDto createOrderDto) {

        String merchantId = createOrderDto.getMerchantId();
        String orderNo = createOrderDto.getOrderNo();
        String merchantAddress = createOrderDto.getMerchantAddress();
        BigDecimal amount = createOrderDto.getAmount();
        String returnAddress = createOrderDto.getReturnAddress();
        String sign = createOrderDto.getSign();
        String payAddress = createOrderDto.getPayAddress();

        RequestLog requestLog = new RequestLog();
        requestLog.setName("??????????????????");
        requestLog.setMerchantId(Long.valueOf(merchantId));
        requestLog.setRequestTime(new Date());
        requestLog.setIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        JSONObject requestJson = new JSONObject();
        requestJson.put("merchantId", merchantId);
        requestJson.put("amount", amount);
        requestJson.put("sign", sign);
        requestJson.put("orderNo", orderNo);
        requestJson.put("returnAddress", returnAddress);
        requestJson.put("payAddress", payAddress);
        requestJson.put("merchantAddress", merchantAddress);
        requestLog.setRequestLog(requestJson.toJSONString());
        requestLog.setRequestTime(new Date());
        requestLog.setType(2);
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {



            R<Object> r =   R.failed(400, "MERCHANT_ID_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }
        if (payAddress == null || "".equals(payAddress) || payAddress.length() < 30 || (!payAddress.startsWith("T"))) {
            R<Object> r =   R.failed(400, "PAY_ADDRESS_ERROR");
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }


        MerchantPayOrder merchantPayOrder = new MerchantPayOrder();
        merchantPayOrder.setAddress(payAddress);
        merchantPayOrder.setMerchantId(merchant.getId() + "");
        merchantPayOrder.setReturnAddress(returnAddress == null ? merchant.getReturnPayAddress() : returnAddress);
        merchantPayOrder.setUsdtBalance(amount);
        merchantPayOrder.setPayAddress(payAddress);
        merchantPayOrder.setHaveUsdt(0);
        merchantPayOrder.setCreateTime(new Date());
        merchantPayOrder.setOrderSn(merchantId + OrderUtil.orderSn() + OrderUtil.createShareCode());
        merchantPayOrder.setStatus(1);
        merchantPayOrder.setPayResult("0");
        merchantPayOrder.setTxid("");
        merchantPayOrder.setPayResult("");
        merchantPayOrder.setMessage("");
        merchantPayOrder.setAgentId(merchant.getAgentId());
        merchantPayOrder.setParams(createOrderDto.getParams());
        merchantPayOrder.setOwnerAddress("");
        merchantPayOrder.setReturnMsg("");
        merchantPayOrder.setRequestLog(requestJson.toJSONString());

        merchantPayOrderMapper.insert(merchantPayOrder);


        boolean ipFlag = false;

        String payApiWhile = merchant.getPayApiWhile();
        if (payApiWhile == null || "".equals(payApiWhile)) {
            ipFlag = true;
            payApiWhile = "127.0.0.1";
        }
        String[] apiWhileSplit = payApiWhile.split(",");

        //??????????????????IP
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        if (ip == null) {
            ip = "127.0.0.1";
        }
        String[] split = ip.split(", ");
        for (String s : split) {
            for (String apiwhile : apiWhileSplit) {
                if (s.equals(apiwhile)) {
                    ipFlag = true;
                    break;
                }
            }
        }
        if (!ipFlag) {
            R<Object> r =   R.failed(900, "IP_ERROR");

            merchantPayOrder.setPayResult("IP??????");
            merchantPayOrder.setMessage("IP??????");
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setMerchantOrderNo(orderNo);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }


        String md5 = SecureUtil.md5(merchant.getSign() + orderNo + payAddress);
        if (!md5.equals(sign)) {
            R<Object> r =   R.failed(401, "SIGN_ERROR");

            merchantPayOrder.setPayResult("????????????");
            merchantPayOrder.setMessage("????????????");
            merchantPayOrder.setMerchantOrderNo(orderNo);

            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrder.setStatus(0);
            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }


        String message = null;
        MerchantPayOrder MerchantPayOrderOne = merchantPayOrderMapper.selectOne(new QueryWrapper<MerchantPayOrder>().eq("merchant_order_no", orderNo).eq("merchant_id", merchantId));
        if (MerchantPayOrderOne != null) {
            R<Object> r =   R.failed(402, "MERCHANT_HAVE_ORDER");

            merchantPayOrder.setPayResult("???????????????");
            merchantPayOrder.setMessage("???????????????");
            merchantPayOrder.setMerchantOrderNo(orderNo + "??????");
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "????????????", "????????????", merchant.getTrxAddress());
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }

//        if (  merchantAddress != null) {
//
//            String key = redisTemplate.opsForValue().get(merchantAddress);
//
//            if (key == null) {
//                return R.failed();
//            }
//            merchant.setUsdtPayAddress(merchantAddress);
//            merchant.setUsdtPayPrivateKey(key);
//        }


        Boolean s = redisTemplate.opsForValue().setIfAbsent(merchantId + ":" + orderNo, orderNo, 1, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(s)) {
            R<Object> r =    R.failed(402, "MERCHANT_HAVE_ORDER");

            merchantPayOrder.setMerchantOrderNo(orderNo + "??????");
            merchantPayOrder.setPayResult("???????????????");
            merchantPayOrder.setMessage("???????????????");
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;
        }
        merchantPayOrder.setMerchantOrderNo(orderNo);


        BigDecimal fee = merchant.getPayFee();
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            R<Object> r =   R.failed(898, "FEE_BALANCE_ERROR");

            merchantPayOrder.setPayResult("???????????????????????????");
            merchantPayOrder.setMessage("???????????????????????????");
            merchantPayOrder.setStatus(4);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "???????????????", "???????????????", merchant.getTrxAddress());



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();

        }

        if (merchant.getBalance() == null || new BigDecimal("100").compareTo(merchant.getBalance()) > 0) {
            sendTelegramMessage(merchant, "?????????????????????", "?????????????????????", merchant.getBalance().toString(), "?????????????????????", "?????????????????????", "???????????????" + merchant.getBalance().toString() + "??????100USDT???????????????");
        }


        if (amount.compareTo(new BigDecimal("1000")) >= 0) {
            R<Object> r =    R.failed(504, "??????????????????????????????????????????????????????");

            MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
            merchantPayOrderAudit.setAddress(merchantPayOrder.getAddress());
            merchantPayOrderAudit.setMerchantId(merchant.getId());
            merchantPayOrderAudit.setReturnAddress(merchantPayOrder.getReturnAddress());
            merchantPayOrderAudit.setUsdtBalance(amount);
            merchantPayOrderAudit.setMerchantOrderNo(orderNo);
            merchantPayOrderAudit.setStatus(0);
            merchantPayOrderAudit.setType(1);
            merchantPayOrderAudit.setFeeAmount(merchant.getPayFee());
            merchantPayOrderAudit.setCreateTime(new Date());
            merchantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayOrder.setPayResult("?????????????????????");
            merchantPayOrder.setMessage("?????????????????????");
            merchantPayOrder.setStatus(4);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();


        }


        List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchantId));
        MerchantPayUseAddress useAddress = null;

        for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
            BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
            if (decimal != null && decimal.compareTo(amount) >= 0) {
                BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                    useAddress = merchantPayUseAddress;
                }

            }
        }

        boolean haveAddress = false;
        for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
            BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
            if (decimal != null && decimal.compareTo(new BigDecimal("100")) >= 0) {
                BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                if (trcBalance != null && trcBalance.compareTo(new BigDecimal("100")) > 0) {
                    haveAddress = true;
                }

            }
        }
        if (!haveAddress) {
            sendTelegramMessage(merchant, "?????????????????????100USDT?????????100TRX?????????", "?????????????????????100USDT?????????100TRX?????????", merchant.getBalance().toString(), "?????????????????????100USDT?????????100TRX????????????????????????", "?????????????????????100USDT?????????100TRX????????????????????????", "?????????????????????100USDT?????????100TRX????????????????????????");
        }




        if (useAddress == null) {
            R<Object> r =   R.failed(403, "USDT_BALANCE_ERROR");

            MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
            merchantPayOrderAudit.setAddress(payAddress);
            merchantPayOrderAudit.setMerchantId(merchant.getId());
            merchantPayOrderAudit.setType(2);
            merchantPayOrderAudit.setReturnAddress(merchantPayOrder.getReturnAddress());
            merchantPayOrderAudit.setUsdtBalance(amount);
            merchantPayOrderAudit.setMerchantOrderNo(orderNo);
            merchantPayOrderAudit.setStatus(0);
            merchantPayOrderAudit.setFeeAmount(merchant.getPayFee());
            merchantPayOrderAudit.setCreateTime(new Date());
            merchantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayOrder.setPayResult("??????????????????");
            merchantPayOrder.setMessage("??????????????????");
            merchantPayOrder.setStatus(4);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "??????????????????", "??????????????????", merchant.getTrxAddress());



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();


        }

        Map<String, String> payMap = trxWallet.createPay(merchant, useAddress, merchantPayOrder);
        String msg = payMap.get("MSG");
        if ("SUCCESS".equals(msg)) {

            R<Object> r =   R.success(payMap);

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(1);
            requestLogMapper.insert(requestLog);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));
            merchantPayOrderMapper.updateById(merchantPayOrder);
            return R.success();

        } else {

            R<Object> r =    R.failed(msg);
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));
            merchantPayOrderMapper.updateById(merchantPayOrder);
            return R.success();

        }
    }


    @RequestMapping("/createPay")
    public Object createPay(@RequestParam(required = false) String params,@RequestParam(required = false) String merchantAddress, @RequestParam String merchantId, @RequestParam String sign, @RequestParam String orderNo, @RequestParam(required = false) String returnAddress, @RequestParam String payAddress, @RequestParam BigDecimal amount) {



        RequestLog requestLog = new RequestLog();
        requestLog.setName("??????????????????");
        requestLog.setMerchantId(Long.valueOf(merchantId));
        requestLog.setRequestTime(new Date());
        requestLog.setIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        JSONObject requestJson = new JSONObject();
        requestJson.put("merchantId", merchantId);
        requestJson.put("amount", amount);
        requestJson.put("sign", sign);
        requestJson.put("orderNo", orderNo);
        requestJson.put("returnAddress", returnAddress);
        requestJson.put("payAddress", payAddress);
        requestJson.put("merchantAddress", merchantAddress);
        requestLog.setRequestLog(requestJson.toJSONString());
        requestLog.setRequestTime(new Date());
        requestLog.setType(2);
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {



            R<Object> r =   R.failed(400, "MERCHANT_ID_ERROR");

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return  r;
        }
            if (payAddress == null || "".equals(payAddress) || payAddress.length() < 30 || (!payAddress.startsWith("T"))) {
                R<Object> r =   R.failed(400, "PAY_ADDRESS_ERROR");
                requestLog.setResponseLog(JSONObject.toJSONString(r));
                requestLog.setStatus(0);
                requestLogMapper.insert(requestLog);
                return  r;

            }


                MerchantPayOrder merchantPayOrder = new MerchantPayOrder();
        merchantPayOrder.setAddress(payAddress);
        merchantPayOrder.setMerchantId(merchant.getId() + "");
        merchantPayOrder.setReturnAddress(returnAddress == null ? merchant.getReturnPayAddress() : returnAddress);
        merchantPayOrder.setUsdtBalance(amount);
        merchantPayOrder.setPayAddress(payAddress);
        merchantPayOrder.setHaveUsdt(0);
        merchantPayOrder.setCreateTime(new Date());
        merchantPayOrder.setOrderSn(merchantId + OrderUtil.orderSn() + OrderUtil.createShareCode());
        merchantPayOrder.setStatus(1);
        merchantPayOrder.setPayResult("0");
        merchantPayOrder.setTxid("");
        merchantPayOrder.setPayResult("");
        merchantPayOrder.setMessage("");
        merchantPayOrder.setAgentId(merchant.getAgentId());
        merchantPayOrder.setParams(params);
        merchantPayOrder.setOwnerAddress("");
        merchantPayOrder.setReturnMsg("");
        merchantPayOrder.setRequestLog(requestJson.toJSONString());

        merchantPayOrderMapper.insert(merchantPayOrder);


        boolean ipFlag = false;

        String payApiWhile = merchant.getPayApiWhile();
        if (payApiWhile == null || "".equals(payApiWhile)) {
            ipFlag = true;
            payApiWhile = "127.0.0.1";
        }
        String[] apiWhileSplit = payApiWhile.split(",");

        //??????????????????IP
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        if (ip == null) {
            ip = "127.0.0.1";
        }
        String[] split = ip.split(", ");
        for (String s : split) {
            for (String apiwhile : apiWhileSplit) {
                if (s.equals(apiwhile)) {
                    ipFlag = true;
                    break;
                }
            }
        }
        if (!ipFlag) {
            R<Object> r =   R.failed(900, "IP_ERROR");

            merchantPayOrder.setPayResult("IP??????");
            merchantPayOrder.setMessage("IP??????");
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setMerchantOrderNo(orderNo);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;

        }


        String md5 = SecureUtil.md5(merchant.getSign() + orderNo + payAddress);
        if (!md5.equals(sign)) {
            R<Object> r =   R.failed(401, "SIGN_ERROR");

            merchantPayOrder.setPayResult("????????????");
            merchantPayOrder.setMessage("????????????");
            merchantPayOrder.setMerchantOrderNo(orderNo);

            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrder.setStatus(0);
            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;

        }


        String message = null;
        MerchantPayOrder MerchantPayOrderOne = merchantPayOrderMapper.selectOne(new QueryWrapper<MerchantPayOrder>().eq("merchant_order_no", orderNo).eq("merchant_id", merchantId));
        if (MerchantPayOrderOne != null) {
            R<Object> r =   R.failed(402, "MERCHANT_HAVE_ORDER");

            merchantPayOrder.setPayResult("???????????????");
            merchantPayOrder.setMessage("???????????????");
            merchantPayOrder.setMerchantOrderNo(orderNo + "??????");
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "????????????", "????????????", merchant.getTrxAddress());





            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;

        }

//        if (  merchantAddress != null) {
//
//            String key = redisTemplate.opsForValue().get(merchantAddress);
//
//            if (key == null) {
//                return R.failed();
//            }
//            merchant.setUsdtPayAddress(merchantAddress);
//            merchant.setUsdtPayPrivateKey(key);
//        }


        Boolean s = redisTemplate.opsForValue().setIfAbsent(merchantId + ":" + orderNo, orderNo, 1, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(s)) {
            R<Object> r =    R.failed(402, "MERCHANT_HAVE_ORDER");

            merchantPayOrder.setMerchantOrderNo(orderNo + "??????");
            merchantPayOrder.setPayResult("???????????????");
            merchantPayOrder.setMessage("???????????????");
            merchantPayOrder.setStatus(0);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return r;


        }
        merchantPayOrder.setMerchantOrderNo(orderNo);


        BigDecimal fee = merchant.getPayFee();
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            R<Object> r =   R.failed(898, "FEE_BALANCE_ERROR");

            merchantPayOrder.setPayResult("???????????????????????????");
            merchantPayOrder.setMessage("???????????????????????????");
            merchantPayOrder.setStatus(4);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "???????????????", "???????????????", merchant.getTrxAddress());



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();


        }

        if (merchant.getBalance() == null || new BigDecimal("100").compareTo(merchant.getBalance()) > 0) {
            sendTelegramMessage(merchant, "?????????????????????", "?????????????????????", merchant.getBalance().toString(), "?????????????????????", "?????????????????????", "???????????????" + merchant.getBalance().toString() + "??????100USDT???????????????");
        }



        if (amount.compareTo(new BigDecimal("1000")) >= 0) {
            R<Object> r =    R.failed(504, "??????????????????????????????????????????????????????");


            MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
            merchantPayOrderAudit.setAddress(merchantPayOrder.getAddress());
            merchantPayOrderAudit.setMerchantId(merchant.getId());
            merchantPayOrderAudit.setReturnAddress(merchantPayOrder.getReturnAddress());
            merchantPayOrderAudit.setUsdtBalance(amount);
            merchantPayOrderAudit.setMerchantOrderNo(orderNo);
            merchantPayOrderAudit.setStatus(0);
            merchantPayOrderAudit.setType(1);
            merchantPayOrderAudit.setFeeAmount(merchant.getPayFee());
            merchantPayOrderAudit.setCreateTime(new Date());
            merchantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayOrder.setPayResult("?????????????????????");
            merchantPayOrder.setMessage("?????????????????????");
            merchantPayOrder.setStatus(4);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();
        }


        List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchantId));
        MerchantPayUseAddress useAddress = null;

        for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
            BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
            if (decimal != null && decimal.compareTo(amount) >= 0) {
                BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                    useAddress = merchantPayUseAddress;
                }

            }
        }

        boolean haveAddress = false;
        for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
            BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
            if (decimal != null && decimal.compareTo(new BigDecimal("100")) >= 0) {
                BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                if (trcBalance != null && trcBalance.compareTo(new BigDecimal("100")) > 0) {
                    haveAddress = true;
                }

            }
        }
        if (!haveAddress) {
            sendTelegramMessage(merchant, "?????????????????????100USDT?????????100TRX?????????", "?????????????????????100USDT?????????100TRX?????????", merchant.getBalance().toString(), "?????????????????????100USDT?????????100TRX????????????????????????", "?????????????????????100USDT?????????100TRX????????????????????????", "?????????????????????100USDT?????????100TRX????????????????????????");
        }




        if (useAddress == null) {
            R<Object> r =   R.failed(403, "USDT_BALANCE_ERROR");

            MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
            merchantPayOrderAudit.setAddress(payAddress);
            merchantPayOrderAudit.setMerchantId(merchant.getId());
            merchantPayOrderAudit.setType(2);
            merchantPayOrderAudit.setReturnAddress(merchantPayOrder.getReturnAddress());
            merchantPayOrderAudit.setUsdtBalance(amount);
            merchantPayOrderAudit.setMerchantOrderNo(orderNo);
            merchantPayOrderAudit.setStatus(0);
            merchantPayOrderAudit.setFeeAmount(merchant.getPayFee());
            merchantPayOrderAudit.setCreateTime(new Date());
            merchantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayOrder.setPayResult("??????????????????");
            merchantPayOrder.setMessage("??????????????????");
            merchantPayOrder.setStatus(4);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));

            merchantPayOrderMapper.updateById(merchantPayOrder);
            sendTelegramMessage(merchant, orderNo, "????????????", amount.toString(), "??????????????????", "??????????????????", merchant.getTrxAddress());



            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            return R.success();




        }

        Map<String, String> payMap = trxWallet.createPay(merchant, useAddress, merchantPayOrder);
        String msg = payMap.get("MSG");
        if ("SUCCESS".equals(msg)) {

            R<Object> r =   R.success(payMap);

            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(1);
            requestLogMapper.insert(requestLog);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));
            merchantPayOrderMapper.updateById(merchantPayOrder);
            return R.success();

        } else {

            R<Object> r =    R.failed(msg);
            requestLog.setResponseLog(JSONObject.toJSONString(r));
            requestLog.setStatus(0);
            requestLogMapper.insert(requestLog);
            merchantPayOrder.setResponseLog(JSONObject.toJSONString(r));
            merchantPayOrderMapper.updateById(merchantPayOrder);
            return null;

        }
    }

    @GetMapping("/getPayMsg")
    public R getPayMsg(@RequestParam String id) {
        return getOrderPayMsg(id);
    }

    private R getOrderPayMsg(String address) {

        for (int i = 0; i < 3600; i++) {
            String hash = redisTemplate.opsForValue().get("orderPayMsg:" + address);
            if (hash == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if ("SUCCESS".equals(hash)) {
                MerchantPayOrder merchantPayOrder = this.merchantPayOrderMapper.selectOne(new QueryWrapper<MerchantPayOrder>().eq("address", address));
                return R.success(merchantPayOrder.getUsdtBalance(), "SUCCESS");
            }
            return R.failed(hash);
        }
        return R.failed();
    }


    @GetMapping("/getPay")
    public R getPay(@RequestParam String address) {
        return getOrder(address);
    }


    public R getOrder(String address) {

        for (int i = 0; i < 3600; i++) {
            String hash = redisTemplate.opsForValue().get("payMsg:" + address);
            if (hash == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if ("SUCCESS".equals(hash)) {
                MerchantPayAddress merchantPayAddress = this.merchantPayAddressMapper.selectOne(new QueryWrapper<MerchantPayAddress>().eq("address", address).orderByDesc("create_time").last(" limit 1"));
                return R.success(merchantPayAddress.getUsdtBalance(), "SUCCESS");
            }
            return R.failed(hash);
        }
        return R.failed();
    }

    public void sendTelegramMessage(Merchant merchant, String orderNo, String orderType, String amount, String title, String content, String address) {

        if (merchant.getTelegramId() == null) {
            return;
        }
        String messgae =
                "\uD83D\uDEA8USDT " + title + "  \n" +
                        "\n" +
                        "<b>???????????????<code>" + orderNo + "</code></b> \n" +
                        "<b>???????????????USDT-TRC20Payout</b> \n" +
                        "<b>???????????????" + amount + "</b> \n" +
                        "<b>???????????????" + orderType + "</b> \n" +
                        "<b>???????????????" + content + "</b> \n" +
                        "<b>?????????<code>" + address + "</code></b> \n" +
                        "<b> ???????????????????????????????????????</b> \n";


        Map<String, Object> map = new HashMap<>();
        map.put("text", messgae);

        map.put("chat_id", merchant.getTelegramId());
        map.put("parse_mode", "HTML");

        String post = HttpUtil.post("https://api.telegram.org/bot" + merchant.getTelegramBotToken() + "/sendMessage", map);
        System.out.println(post);

    }
}

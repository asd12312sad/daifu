package com.ms.student.Task;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ms.common.core.utils.DateUtils;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.student.domain.*;
import com.ms.student.mapper.*;
import com.ms.student.service.MerchantCollectUseAddressService;
import com.ms.student.service.OrderService;
import com.ms.student.service.impl.TRXWallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Recharge {

    @Autowired
    private OrderService orderService;


    @Autowired
    private TRXWallet trxWallet;

    @Autowired
    private MerchantPayAddressMapper merchantPayAddressMapper;

    @Autowired
    private MerchantCollectUseAddressService merchantCollectUseAddressService;
    @Autowired
    private MerchantPayOrderAuditMapper merchantPayOrderAuditMapper;

    @Autowired
    private  MerchantPayUseAddressMapper merchantPayUseAddressMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private MerchantPayOrderMapper merchantPayOrderMapper;
    private final static String contractAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";


    //每一秒请求一次
    @Scheduled(cron = "0/1 * * * * ? ")
    public void recharge() {

        Date date = DateUtils.addMinutes(new Date(), -59);
        List<MerchantPayAddress> merchantPayAddresses = merchantPayAddressMapper.selectList(new QueryWrapper<MerchantPayAddress>().eq("status", 0).gt("create_time", date));
        for (MerchantPayAddress merchantPayAddress : merchantPayAddresses) {

            this.trxWallet.lockAddress(merchantPayAddress.getAddress());
            String json = HttpUtil.get("https://api.trongrid.io/v1/accounts/" + merchantPayAddress.getAddress() + "/transactions/trc20?only_to=true&limit=50&contract_address=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                String transactionId = data.getJSONObject(i).getString("transaction_id");
                orderService.inform(transactionId, merchantPayAddress);
            }
            this.trxWallet.unLockAddress(merchantPayAddress.getAddress());
        }
        //逾期未支付
        date = DateUtils.addMinutes(date,-2);
        merchantPayAddresses = merchantPayAddressMapper.selectList(new QueryWrapper<MerchantPayAddress>().eq("status", 0).le("create_time", date));
        for (MerchantPayAddress merchantPayAddress : merchantPayAddresses) {
            this.trxWallet.lockAddress(merchantPayAddress.getAddress());
                MerchantCollectUseAddress collectUseAddress = merchantCollectUseAddressService.getOne(new QueryWrapper<MerchantCollectUseAddress>().eq("order_sn", merchantPayAddress.getOrderSn()));
                if (collectUseAddress!=null){
                    merchantCollectUseAddressService.update(new UpdateWrapper<MerchantCollectUseAddress>().set("order_sn",null).eq("id",collectUseAddress.getId()));
                }
                merchantPayAddress.setStatus(7);
                merchantPayAddressMapper.updateById(merchantPayAddress);
               this.trxWallet.unLockAddress(merchantPayAddress.getAddress());

        }
    }


    //每一秒请求一次
    @Scheduled(cron = "0 10 * * * ? ")
    public void telegramMessage() {
        List<Merchant> merchantList = merchantMapper.selectList(new QueryWrapper<Merchant>().isNotNull("telegram_id"));
        for (Merchant merchant : merchantList) {
            if (merchant.getBalance() == null || new BigDecimal("100").compareTo(merchant.getBalance()) > 0) {
                sendTelegramMessage(merchant, "账户手续费余额", "商户手续费不足", merchant.getBalance().toString() ,"商户手续费不足","商户手续费不足","当前手续费" + merchant.getBalance().toString() + "不足100USDT请及时充值");
            }

            BigDecimal trxBalance = trxWallet.balanceOfTron(merchant.getTrxAddress());
            log.info(merchant.getTrxAddress() + "==========商户TRX手续费===========" + trxBalance);

            if (trxBalance.compareTo(new BigDecimal("100")) < 0) {
                sendTelegramMessage(merchant, merchant.getTrxAddress(),"代收TRX矿工费",merchant.getTrxAddress(), trxBalance.toString(), "商户TRX手续费不足", "当前矿工费" +trxBalance.toString() + "不足100TRX请及时充值");
            }

            List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchant.getId()));
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
            if (!haveAddress){
                sendTelegramMessage(merchant, "无满足余额大于100USDT并大于100TRX的账户", "无满足余额大于100USDT并大于100TRX的账户", merchant.getBalance().toString() ,"无满足余额大于100USDT并大于100TRX的账户请及时充值","无满足余额大于100USDT并大于100TRX的账户请及时充值","无满足余额大于100USDT并大于100TRX的账户请及时充值");

            }
        }
    }
    public void sendTelegramMessage(Merchant merchant, String orderNo, String orderType, String amount, String title, String content, String address) {

        if (merchant.getTelegramId() == null) {
            return;
        }
        String messgae =
                "\uD83D\uDEA8USDT " + title + "  \n" +
                        "\n" +
                        "<b>订单单号：<code>" + orderNo + "</code></b> \n" +
                        "<b>订单产品：USDT-TRC20Payout</b> \n" +
                        "<b>订单金额：" + amount + "</b> \n" +
                        "<b>账户名称：" + orderType + "</b> \n" +
                        "<b>错误消息：" + content + "</b> \n" +
                        "<b>地址：<code>" + address + "</code></b> \n" +
                        "<b> 点击上述地址可复制到粘贴板</b> \n";


        Map<String, Object> map = new HashMap<>();
        map.put("text", messgae);

        map.put("chat_id", merchant.getTelegramId());
        map.put("parse_mode", "HTML");

        String post = HttpUtil.post("https://api.telegram.org/bot" + merchant.getTelegramBotToken() + "/sendMessage", map);
        System.out.println(post);

    }

    //每一秒请求一次
    @Scheduled(cron = "0/1 * * * * ? ")
    public void rechargePay() {
        List<MerchantPayOrder> merchantPayAddresses = merchantPayOrderMapper.selectList(new QueryWrapper<MerchantPayOrder>().eq("status", 1));
        for (MerchantPayOrder merchantPayAddress : merchantPayAddresses) {
            String restJson = trxWallet.getTransactionById(merchantPayAddress.getTxid());
            log.error(restJson);

            JSONObject jsonObject = JSONObject.parseObject(restJson);
            if (jsonObject.getString("result") == null){
                JSONObject receipt = jsonObject.getJSONObject("receipt");
                if (receipt == null){
                    continue;
                }
                if (receipt.getString("result") == null){
                    continue;
                }
            }
            orderService.rechargePay(jsonObject, merchantPayAddress);


        }
    }


    //每一分钟请求一次队列
    @Scheduled(cron = "0/1 * * * * ? ")
    public void queuePay() {
        List<MerchantPayOrder> merchantPayAddresses = merchantPayOrderMapper.selectList(new QueryWrapper<MerchantPayOrder>().eq("status", 3).gt("create_time", DateUtils.addMinutes(new Date(), -30)));
        for (MerchantPayOrder merchantPayAddress : merchantPayAddresses) {
            Merchant merchant = merchantMapper.selectById(merchantPayAddress.getMerchantId());
            BigDecimal fee =merchant.getPayFee();
            if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
                log.error("商户手续费不足请联系商户处理");
                continue;
            }

            List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchant.getId()));
            MerchantPayUseAddress useAddress = null;
            for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
                BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
                if (decimal != null && decimal.compareTo(merchantPayAddress.getUsdtBalance()) >= 0) {
                    BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                    if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                        useAddress = merchantPayUseAddress;
                    }
                }
            }
            if (useAddress!=null){
                Map<String, String> payMap = trxWallet.createPayByQueue(merchant, useAddress,merchantPayAddress);
                String msg = payMap.get("MSG");
                if (msg.equals("SUCCESS")) {
                    merchantPayAddress.setStatus(1);
                    merchantPayOrderMapper.updateById(merchantPayAddress);
                    continue;
                } else {
                    continue;
                }


            }
        }
    }


    @Scheduled(cron = "0/1 * * * * ? ")
    public void rechargeRecharge() {
        List<Merchant> merchantList = merchantMapper.selectList(new QueryWrapper<>());

        for (Merchant merchant : merchantList) {
            if (merchant.getRechargeAddress() != null) {
                String json = HttpUtil.get("https://api.trongrid.io/v1/accounts/" + merchant.getRechargeAddress() + "/transactions/trc20?only_to=true&limit=10&contract_address=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");
                JSONObject jsonObject = JSONObject.parseObject(json);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.size(); i++) {
                    String transactionId = data.getJSONObject(i).getString("transaction_id");

                    orderService.rechargeRecharge(transactionId, merchant);
                }
            }
        }

    }


    @Scheduled(cron = "0 */30 * * * ? ")
    public void replenishment() {


        Date date = DateUtils.addDays(new Date(), -1);

        //0:待支付
        //1:支付成功
        //2:付款金额与订单金额不匹配，自动补单
        //3:商户手续费不足，请充值Pay cloud，系统将在30分钟后自动回调
        //4:代收地址TRX矿工费余额不足，无法归集
        //5：返回回调失败
        //6: 支付过程错误，请联系客服
        //7：逾期未支付
        List<MerchantPayAddress> addressList = merchantPayAddressMapper.selectList(new QueryWrapper<MerchantPayAddress>().in("status", 3, 4, 5).gt("create_time", date));

        for (MerchantPayAddress merchantPayAddress : addressList) {
            this.trxWallet.lockAddress(merchantPayAddress.getAddress());
            orderService.replenishment(merchantPayAddress);
            this.trxWallet.unLockAddress(merchantPayAddress.getAddress());
        }
    }

    @Scheduled(cron = "0 */10 * * * ? ")
    public void replenishmentPay() {

        Date date = DateUtils.addDays(new Date(), -1);

        List<MerchantPayOrderAudit> payOrderAuditList = merchantPayOrderAuditMapper.selectList(new QueryWrapper<MerchantPayOrderAudit>().eq("status", 0).eq("type", 2).ge("create_time",date));
        for (MerchantPayOrderAudit merchantPayOrderAudit : payOrderAuditList) {

            MerchantPayOrder merchantPayOrder = merchantPayOrderMapper.selectOne(new QueryWrapper<MerchantPayOrder>().eq("status", 4).eq("merchant_order_no", merchantPayOrderAudit.getMerchantOrderNo()).eq("merchant_id",merchantPayOrderAudit.getMerchantId()));

           if (merchantPayOrder==null){
               continue;
           }
            boolean lockFlag = this.trxWallet.lockPayOrder(merchantPayOrder);
            if (!lockFlag){
                continue;
            }
            Merchant merchant = merchantMapper.selectById(merchantPayOrderAudit.getMerchantId());
            BigDecimal fee =merchant.getPayFee();
            if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
                log.error("商户手续费不足请联系商户处理");
                continue;
            }
            if (merchantPayOrder == null) {
                continue;
            }
            List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchantPayOrderAudit.getMerchantId()));
            MerchantPayUseAddress useAddress = null;
            for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
                BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
                if (decimal != null && decimal.compareTo(merchantPayOrderAudit.getUsdtBalance()) >= 0) {
                    BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                    if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                        useAddress = merchantPayUseAddress;
                    }
                }
            }
            if (useAddress!=null){

                Map<String, String> payMap = trxWallet.createPay(merchant, useAddress, merchantPayOrder);
                String msg = payMap.get("MSG");
                if (msg.equals("SUCCESS")) {
                    merchantPayOrderAudit.setStatus(1);
                    merchantPayOrderAuditMapper.updateById(merchantPayOrderAudit);
                    continue;
                } else {
                  continue;
                }
            }
            trxWallet.unLockPayOrder(merchantPayOrder.getId());
        }
    }

    //检查归集
    @Scheduled(cron = "0 */1 * * * ? ")
    public void foGather() {


        //如果单个账户超过目标归集金额，则自动归集
        List<MerchantCollectUseAddress> collectUseAddressList = merchantCollectUseAddressService.list(new QueryWrapper<MerchantCollectUseAddress>().eq("status",0));
       if (collectUseAddressList.size()==0){
           return;
       }
        //取出所有merchantId
        List<Long> merchantIdList = collectUseAddressList.stream().map(MerchantCollectUseAddress::getMerchantId).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);


        List<Merchant> merchants = merchantMapper.selectBatchIds(merchantIdList);

        for (MerchantCollectUseAddress merchantCollectUseAddress : collectUseAddressList) {
            for (Merchant merchant : merchants) {
                if (merchant.getId().compareTo(merchantCollectUseAddress.getMerchantId())==0){
                    try {
                        merchantCollectUseAddress.setAmount(trxWallet.usdtBalanceOf(merchantCollectUseAddress.getAddress()));

                        merchantCollectUseAddressService.updateById(merchantCollectUseAddress);
                    }catch (Exception e){
                        continue;
                    }

                    if (merchantCollectUseAddress.getAmount().compareTo(merchant.getNotionalPoolingAmount())>0){
                        this.trxWallet.lockAddress(merchantCollectUseAddress.getAddress());
                        try {
                            orderService.foGather(merchantCollectUseAddress, merchant);
                        }catch (Exception e){
                            continue;
                        }
                        this.trxWallet.unLockAddress(merchantCollectUseAddress.getAddress());

                    }
                }
            }
        }

    }




}

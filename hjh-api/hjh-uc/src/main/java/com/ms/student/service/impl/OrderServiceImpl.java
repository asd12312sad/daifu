package com.ms.student.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.core.utils.DateUtils;
import com.ms.common.domain.R;
import com.ms.student.domain.*;
import com.ms.student.mapper.*;
import com.ms.student.service.MerchantCollectUseAddressService;
import com.ms.student.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private TransferMapper transferMapper;

    @Autowired
    private TRXWallet trxWallet;


    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;


    @Autowired
    private MerchantCollectUseAddressService merchantCollectUseAddressService;
    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private MerchantPayOrderAuditMapper merhantPayOrderAuditMapper;
    @Autowired
    private LogRechargeMapper logRechargeMapper;
    @Autowired
    private MerchantPayAddressMapper merchantPayAddressMapper;
    @Autowired
    private MerchantPayOrderMapper merchantPayOrderMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Async
    public void test(int i) {
        StringBuilder phone = new StringBuilder("09");

        String iString = i + "";

        //iString ?????????0????????????
        iString = iString.length() < 3 ? "0" + iString : iString;
        iString = iString.length() < 3 ? "0" + iString : iString;
        iString = iString.length() < 3 ? "0" + iString : iString;

        phone.append(iString);

        // ????????????
        for (int j = 0; j < 100000; j++) {
            String newPhone = phone.toString() + "";
            String iString2 = j + "";
            iString2 = iString2.length() < 5 ? "0" + iString2 : iString2;
            iString2 = iString2.length() < 5 ? "0" + iString2 : iString2;
            iString2 = iString2.length() < 5 ? "0" + iString2 : iString2;
            iString2 = iString2.length() < 5 ? "0" + iString2 : iString2;
            iString2 = iString2.length() < 5 ? "0" + iString2 : iString2;
            newPhone = newPhone + iString2;

            Map<String, Object> postJson = new HashMap<>();
            postJson.put("account", newPhone);
            postJson.put("password", "29856872yi3ury261649");
            try {
                String post = HttpUtil.post("https://www.ynnsnezx.com/api/vue/user/login", JSON.toJSONString(postJson));
                System.out.println(post);
                //?????? post ?????? ???
                if (post.contains("?????????????????????")) {
                    System.out.println(newPhone + "??????");
                    //??????1.txt
                    String content = newPhone + "\n";
                    FileUtil.appendString(content, "/Users/xiaobing/Desktop/phone.txt", StandardCharsets.UTF_8);

                } else if (post.contains("???????????????")) {
                    //??????1.txt
                    String content = newPhone + "\n";
                    FileUtil.appendString(content, "/Users/xiaobing/Desktop/phoneSB.txt", StandardCharsets.UTF_8);

                    System.out.println(newPhone + "?????????");
                } else {
                    System.out.println(newPhone + "??????");

                    //??????1.txt
                    String content = newPhone + post + "\n";
                    FileUtil.appendString(content, "/Users/xiaobing/Desktop/phoneERROR.txt", StandardCharsets.UTF_8);

                }
            } catch (Exception e) {
                System.out.println(newPhone + "??????");

                //??????1.txt
                String content = newPhone + "\n";
                FileUtil.appendString(content, "/Users/xiaobing/Desktop/phoneERROR.txt", StandardCharsets.UTF_8);

                e.printStackTrace();
            }

        }
    }


    @Override
    @Transactional
    public void inform(String transactionId, MerchantPayAddress merchantPayAddress) {
        log.info("tokenTransferInfo===========" + transactionId);

        if (merchantPayAddressMapper.selectCount(new QueryWrapper<MerchantPayAddress>().eq("tx_id",transactionId))!=0){
            return;
        }


        Transfer transfer = transferMapper.selectById(transactionId);
        if (transfer == null) {
            transfer = new Transfer();
            transfer.setTrId(transactionId);
            transfer.setCreateTime(new Date());
            transfer.setStatus(0);
            transferMapper.insert(transfer);
        }
        merchantPayAddress.setTxId(transactionId);
        String msg = "";
        String json = HttpUtil.get("https://apilist.tronscan.org/api/transaction-info?hash=" + transactionId);
        JSONObject jsonObject = JSONObject.parseObject(json);

        String contractRet = jsonObject.getString("contractRet");
        System.out.println(json);
        String userAddress = jsonObject.getString("ownerAddress");
        if (!contractRet.equals("SUCCESS")) {
            msg = "????????????";
            transfer.setMsg(msg);
            merchantPayAddress.setMessage("????????????");
            merchantPayAddress.setStatus(6);
            merchantPayAddressMapper.updateById(merchantPayAddress);
            transferMapper.updateById(transfer);
            return;
        }
        String amount = null;
        if (jsonObject.getJSONObject("tokenTransferInfo") == null) {
            amount = jsonObject.getJSONArray("trc20ApprovalInfo").getJSONObject(0).getString("amount_str");
        } else {
            amount = jsonObject.getJSONObject("tokenTransferInfo").getString("amount_str");
        }
        if (amount == null) {
            msg = "??????????????????";
            transfer.setMsg(msg);
            merchantPayAddress.setStatus(6);
            merchantPayAddress.setMessage("??????????????????");
            merchantPayAddressMapper.updateById(merchantPayAddress);
            transferMapper.updateById(transfer);
            redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "??????????????????", 30, TimeUnit.MINUTES);
            return;
        }
        BigDecimal bigDecimal = new BigDecimal(amount).divide(new BigDecimal("1000000"));
        merchantPayAddress.setUsdtBalance(bigDecimal);
        merchantPayAddress.setPayAddress(userAddress);
        merchantPayAddress.setHaveUsdt(1);
        Merchant merchant = merchantMapper.selectById(merchantPayAddress.getMerchantId());
        String md5 = SecureUtil.md5(merchant.getSign() + bigDecimal + userAddress);
        String returnMsg = "Exception";
        try {
            returnMsg = HttpUtil.get(merchantPayAddress.getReturnAddress() + "?amount=" + bigDecimal + "&sign=" + md5 + "&ownerAddress=" + userAddress + "&orderNo=" + merchantPayAddress.getMerchantOrderNo() + "&params=" + merchantPayAddress.getParams() + "&sysOrderNo=" + merchantPayAddress.getOrderSn());
        } catch (Exception e) {
            e.printStackTrace();
        }

        merchantPayAddress.setMessage(returnMsg);
        merchantPayAddress.setReceivedAmount(bigDecimal);
        if (bigDecimal.compareTo(merchantPayAddress.getOrderAmount()) != 0) {
//            msg = "???????????????????????????????????????????????????";
           msg = "???????????????????????????????????????????????????";

            transfer.setMsg(msg);
//            merchantPayAddress.setStatus(2);
            merchantPayAddress.setMessage("???????????????????????????????????????????????????");
            merchantPayAddressMapper.updateById(merchantPayAddress);
            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "???????????????????????????????????????????????????", "????????????" + merchantPayAddress.getOrderAmount().toString() + "????????????" + bigDecimal.toString(), merchantPayAddress.getAddress());
            transferMapper.updateById(transfer);
            redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "???????????????????????????????????????????????????", 30, TimeUnit.MINUTES);
            return;

        }


//        BigDecimal trxBalance = trxWallet.balanceOfTron(merchant.getTrxAddress());
//        log.info(merchant.getTrxAddress() + "==========??????TRX?????????===========" + trxBalance);

//        if (trxBalance.compareTo(BigDecimal.TEN) < 0) {
//            log.error("??????TRX??????????????????????????????????????????????????????");
//            merchantPayAddress.setStatus(4);
//            merchantPayAddress.setMessage("??????TRX?????????????????????????????????,?????????????????????");
//            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "??????TRX???????????????", "???????????????" + BigDecimal.TEN.toString() + "????????????" + trxBalance.toString(), merchantPayAddress.getAddress());
//            merchantPayAddressMapper.updateById(merchantPayAddress);
//            redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "??????TRX?????????????????????????????????,?????????????????????", 30, TimeUnit.MINUTES);
//            return;
//        }

//        if (trxBalance.compareTo(new BigDecimal("100")) < 0) {
//            sendTelegramMessage(merchant, merchant.getTrxAddress(), "??????TRX?????????", merchant.getTrxAddress(), trxBalance.toString(), "??????TRX???????????????", "???????????????" + trxBalance.toString() + "??????100TRX???????????????");
//        }

        BigDecimal fee = bigDecimal.multiply(merchant.getFee());
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            log.error("??????????????????????????????????????????");
            merchantPayAddress.setStatus(3);
            merchantPayAddress.setMessage("??????????????????????????????????????????");
            merchantPayAddressMapper.updateById(merchantPayAddress);
            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "?????????????????????", "???????????????" + fee.toString() + "????????????" + merchant.getFee().toString(), merchantPayAddress.getAddress());
            redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "??????????????????????????????????????????", 30, TimeUnit.MINUTES);
            return;
        }
        if (merchant.getBalance() == null || new BigDecimal("100").compareTo(merchant.getBalance()) > 0) {
            sendTelegramMessage(merchant, "?????????????????????", "?????????????????????", merchant.getBalance().toString(), "?????????????????????", "?????????????????????", "???????????????" + merchant.getBalance().toString() + "??????100USDT???????????????");
        }

        //??????????????????10trx
//        trxWallet.sendTransaction(merchant.getTrxPrivateKey(), merchant.getTrxAddress(), merchantPayAddress.getAddress(), BigDecimal.TEN, merchantPayAddress.getOrderSn());
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        merchant.setPresentedBalance(merchant.getPresentedBalance().subtract(fee));
        if (merchant.getPresentedBalance().compareTo(BigDecimal.ZERO) < 0) {
            if (merchant.getAgentId() != null) {
                Agent agent = agentMapper.selectById(merchant.getAgentId());
                if (agent != null) {

                    if (agent.getFee() != null) {
                        BigDecimal agentFee = bigDecimal.multiply(agent.getFee());
                        //??????
                        merchantPayAddress.setAgentFee(agentFee);
                        BigDecimal profit = agentFee.subtract(fee);
                        agent.setUnbalancedAmount(agent.getUnbalancedAmount().add(profit));
                        agentMapper.updateById(agent);
                    } else {
                        merchantPayAddress.setAgentFee(BigDecimal.ZERO);

                    }
                } else {
                    merchantPayAddress.setAgentFee(BigDecimal.ZERO);
                }
            } else {
                merchantPayAddress.setAgentFee(BigDecimal.ZERO);
            }
        } else {
            merchantPayAddress.setAgentFee(fee);
        }
        merchant.setBalance(merchant.getBalance().subtract(fee));
        merchantPayAddress.setFeeAmount(fee);
        merchantMapper.updateById(merchant);


//        trxBalance = trxWallet.balanceOfTron(merchantPayAddress.getAddress());
//        log.info(merchantPayAddress.getAddress() + "==========trxBalance===========" + trxBalance);
//
//        //??????
//        String result = trxWallet.usdtSendTransaction(merchantPayAddress.getAddress(), merchantPayAddress.getPrivateKey(), bigDecimal, merchant.getUsdtAddress(), merchantPayAddress.getOrderSn());

        MerchantCollectUseAddress collectUseAddress = merchantCollectUseAddressService.getOne(new QueryWrapper<MerchantCollectUseAddress>().eq("order_sn", merchantPayAddress.getOrderSn()));
        collectUseAddress.setAmount(collectUseAddress.getAmount().add(bigDecimal));
        merchantCollectUseAddressService.update(new UpdateWrapper<MerchantCollectUseAddress>().set("order_sn",null).set("amount",collectUseAddress.getAmount()).eq("id",collectUseAddress.getId()));



        redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "SUCCESS", 30, TimeUnit.MINUTES);
        merchantPayAddress.setReturnMsg(merchantPayAddress.getReturnAddress() + "?amount=" + bigDecimal + "&sign=" + md5 + "&ownerAddress=" + userAddress + "&orderNo=" + merchantPayAddress.getMerchantOrderNo() + "&params=" + merchantPayAddress.getParams() + "&sysOrderNo=" + merchantPayAddress.getOrderSn());
        merchantPayAddress.setReturnTime(new Date());

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("amount", merchantPayAddress.getUsdtBalance());
        jsonObject1.put("sign", md5);
        jsonObject1.put("ownerAddress", merchantPayAddress.getOwnerAddress());
        jsonObject1.put("orderNo", merchantPayAddress.getMerchantOrderNo());
        merchantPayAddress.setReturnRequestLog(jsonObject1.toJSONString());

        if (!(returnMsg == null || "".equals(returnMsg) || "Exception".equals(returnMsg))) {
            merchantPayAddress.setReturnResponseLog(returnMsg);
            if (returnMsg.toUpperCase().equals("SUCCESS")) {
                if (merchantPayAddress.getStatus() != 2) {
                    merchantPayAddress.setStatus(1);
                }
                merchantPayAddress.setSuccessTime(new Date());
                merchantPayAddress.setMessage("SUCCESS");
            } else {
                MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
                merchantPayOrderAudit.setAddress(userAddress);
                merchantPayOrderAudit.setMerchantId(merchant.getId());
                merchantPayOrderAudit.setReturnAddress(merchantPayAddress.getReturnAddress());
                merchantPayOrderAudit.setUsdtBalance(bigDecimal);
                merchantPayOrderAudit.setMerchantOrderNo(merchantPayAddress.getMerchantOrderNo());
                merchantPayOrderAudit.setStatus(0);
                merchantPayOrderAudit.setType(3);
                merchantPayOrderAudit.setOwnerAddress(merchantPayAddress.getAddress());
                merchantPayOrderAudit.setFeeAmount(merchantPayAddress.getFeeAmount());
                merchantPayOrderAudit.setCreateTime(new Date());
                merhantPayOrderAuditMapper.insert(merchantPayOrderAudit);
                merchantPayAddress.setStatus(5);
                merchantPayAddress.setMessage(returnMsg);
                sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "??????????????????", returnMsg, merchantPayAddress.getAddress());
            }
        } else {

            MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
            merchantPayOrderAudit.setAddress(userAddress);
            merchantPayOrderAudit.setMerchantId(merchant.getId());
            merchantPayOrderAudit.setReturnAddress(merchantPayAddress.getReturnAddress());
            merchantPayOrderAudit.setUsdtBalance(bigDecimal);
            merchantPayOrderAudit.setMerchantOrderNo(merchantPayAddress.getMerchantOrderNo());
            merchantPayOrderAudit.setStatus(0);
            merchantPayOrderAudit.setType(3);
            merchantPayOrderAudit.setOwnerAddress(merchantPayAddress.getAddress());
            merchantPayOrderAudit.setFeeAmount(merchantPayAddress.getFeeAmount());
            merchantPayOrderAudit.setCreateTime(new Date());
            merhantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayAddress.setStatus(5);
            merchantPayAddress.setMessage("??????????????????");
            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "??????????????????", "??????????????????", merchantPayAddress.getAddress());
        }

        merchantPayAddressMapper.updateById(merchantPayAddress);


    }


//    public static void main(String[] args) {
//
//
//        String md5 = SecureUtil.md5("3dc86ace717e992b4510b1fa2ff87d1b" + "0.01" + "TREsnu7YhYJqVLzDpUdjXtpaERCoHZLyed");
//
//       String url =  "https://apix83.oltgzn888.com/PayCloudPayReturn.php" + "?amount=" + "0.01" + "&sign=" + md5 + "&ownerAddress=" + "TREsnu7YhYJqVLzDpUdjXtpaERCoHZLyed" + "&orderNo=" +"lin202209010170011" + "&params=" +"" + "&sysOrderNo=" + "sys07386";
//
//        System.out.println(url);
//        String s = HttpUtil.get(url);
//        System.out.println(s);
//
//    }
    @Override
    public void rechargePay(JSONObject jsonObject, MerchantPayOrder merchantPayAddress) {
        log.info("tokenTransferInfo===========" + jsonObject.toJSONString());
        String result = jsonObject.getString("result");
        if (result == null) {
            result = jsonObject.getJSONObject("receipt").getString("result");

        }
        if ("SUCCESS".equals(result)) {
            merchantPayAddress.setHaveUsdt(1);
            merchantPayAddress.setStatus(2);
            merchantPayOrderMapper.updateById(merchantPayAddress);
            Merchant merchant = merchantMapper.selectById(merchantPayAddress.getMerchantId());
            BigDecimal fee = merchant.getPayFee();
            merchantPayAddress.setHaveUsdt(1);
            merchant.setPresentedBalance(merchant.getPresentedBalance().subtract(fee));
            if (merchant.getPresentedBalance().compareTo(BigDecimal.ZERO) < 0) {
                if (merchant.getAgentId() != null) {
                    Agent agent = agentMapper.selectById(merchant.getAgentId());
                    if (agent != null) {

                        if (agent.getFee() != null) {
                            BigDecimal agentFee = agent.getPayFee();
                            //??????
                            merchantPayAddress.setAgentFee(agentFee);
                            BigDecimal profit = agentFee.subtract(fee);
                            agent.setUnbalancedAmount(agent.getUnbalancedAmount().add(profit));
                            agentMapper.updateById(agent);
                        } else {
                            merchantPayAddress.setAgentFee(BigDecimal.ZERO);

                        }
                    } else {
                        merchantPayAddress.setAgentFee(BigDecimal.ZERO);
                    }
                } else {
                    merchantPayAddress.setAgentFee(BigDecimal.ZERO);
                }
            } else {
                merchantPayAddress.setAgentFee(fee);
            }

            merchant.setBalance(merchant.getBalance().subtract(fee));
            merchantPayAddress.setFeeAmount(fee);

            if (merchant.getAgentId() != null) {
                Agent agent = agentMapper.selectById(merchant.getAgentId());
                if (agent != null) {
                    BigDecimal agentFee = agent.getPayFee();

                    if (agentFee != null) {
                        //??????
                        merchantPayAddress.setAgentFee(agentFee);
                        BigDecimal profit = agentFee.subtract(fee);
                        agent.setUnbalancedAmount(agent.getUnbalancedAmount().add(profit));
                        agentMapper.updateById(agent);
                    } else {
                        merchantPayAddress.setAgentFee(BigDecimal.ZERO);

                    }

                } else {
                    merchantPayAddress.setAgentFee(BigDecimal.ZERO);

                }
            } else {
                merchantPayAddress.setAgentFee(BigDecimal.ZERO);

            }
            merchantMapper.updateById(merchant);


            String md5 = SecureUtil.md5(merchant.getSign() + merchantPayAddress.getUsdtBalance() + merchantPayAddress.getOwnerAddress());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //??????
            merchantPayAddress.setReturnMsg(merchantPayAddress.getReturnAddress() + "?amount=" + merchantPayAddress.getUsdtBalance() + "&sign=" + md5 + "&ownerAddress=" + merchantPayAddress.getOwnerAddress() + "&orderNo=" + merchantPayAddress.getMerchantOrderNo() + "&params=" + merchantPayAddress.getParams() + "&sysOrderNo=" + merchantPayAddress.getOrderSn());
            merchantPayAddress.setReturnTime(new Date());

            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("amount", merchantPayAddress.getUsdtBalance());
                jsonObject1.put("sign", md5);
                jsonObject1.put("ownerAddress", merchantPayAddress.getOwnerAddress());
                jsonObject1.put("orderNo", merchantPayAddress.getMerchantOrderNo());
                merchantPayAddress.setReturnRequestLog(jsonObject1.toJSONString());
                String returnMsg = HttpUtil.get(merchantPayAddress.getReturnAddress() + "?amount=" + merchantPayAddress.getUsdtBalance() + "&sign=" + md5 + "&ownerAddress=" + merchantPayAddress.getOwnerAddress() + "&orderNo=" + merchantPayAddress.getMerchantOrderNo() + "&params=" + merchantPayAddress.getParams() + "&sysOrderNo=" + merchantPayAddress.getOrderSn());
                merchantPayAddress.setReturnResponseLog(returnMsg);
                if (returnMsg != null && returnMsg.toUpperCase().equals("SUCCESS")) {
                    merchantPayAddress.setSuccessTime(new Date());
                    merchantPayAddress.setMessage("SUCCESS");
                } else {
                    merchantPayAddress.setMessage(returnMsg);
                    sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getUsdtBalance().toString(), "????????????", returnMsg, merchantPayAddress.getOwnerAddress());

                }
            } catch (Exception e) {
                merchantPayAddress.setMessage("??????????????????");
                sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getUsdtBalance().toString(), "??????????????????", "??????????????????", merchantPayAddress.getOwnerAddress());
            }
            merchantPayOrderMapper.updateById(merchantPayAddress);

        } else {
            merchantPayAddress.setMessage("????????????-???????????????");
                this.realPay(merchantPayAddress);
            merchantPayOrderMapper.updateById(merchantPayAddress);
            System.out.println(jsonObject.getString("result"));
        }
    }

    public void realPay(MerchantPayOrder merchantPayAddress) {
        MerchantPayUseAddress useAddress = null;
        List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", merchantPayAddress.getMerchantId()));

        for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
            BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
            if (decimal != null && decimal.compareTo(merchantPayAddress.getUsdtBalance()) >= 0) {
                BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                    useAddress = merchantPayUseAddress;
                }

            }
        }
        if (useAddress == null) {
            MerchantPayOrderAudit merchantPayOrderAudit = new MerchantPayOrderAudit();
            merchantPayOrderAudit.setAddress(merchantPayAddress.getPayAddress());
            merchantPayOrderAudit.setMerchantId(Long.parseLong(merchantPayAddress.getMerchantId()));
            merchantPayOrderAudit.setType(2);
            merchantPayOrderAudit.setReturnAddress(merchantPayAddress.getReturnAddress());
            merchantPayOrderAudit.setUsdtBalance(merchantPayAddress.getUsdtBalance());
            merchantPayOrderAudit.setMerchantOrderNo(merchantPayAddress.getMerchantOrderNo());
            merchantPayOrderAudit.setStatus(0);
            merchantPayOrderAudit.setFeeAmount(merchantPayAddress.getFeeAmount());
            merchantPayOrderAudit.setCreateTime(new Date());
            merhantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayAddress.setPayResult("??????????????????");
            merchantPayAddress.setMessage("??????????????????");
            merchantPayAddress.setStatus(4);
            merchantPayOrderMapper.updateById(merchantPayAddress);
            return;
        }


        Merchant merchant = merchantMapper.selectById(merchantPayAddress.getMerchantId());


        Map<String, String> payMap = trxWallet.createPay(merchant, useAddress, merchantPayAddress);
        merchantPayOrderMapper.updateById(merchantPayAddress);

    }



    @Override
    public void rechargeRecharge(String transactionId, Merchant merchant) {
        Transfer transfer = transferMapper.selectById(transactionId);
        if (transfer == null) {
            transfer = new Transfer();
            transfer.setTrId(transactionId);
            transfer.setCreateTime(new Date());
            transfer.setStatus(0);
            transferMapper.insert(transfer);

            String msg = "";
            String json = HttpUtil.get("https://apilist.tronscan.org/api/transaction-info?hash=" + transactionId);
            JSONObject jsonObject = JSONObject.parseObject(json);
            String contractRet = jsonObject.getString("contractRet");
            System.out.println(json);
            String userAddress = jsonObject.getString("ownerAddress");
            if (!contractRet.equals("SUCCESS")) {
                msg = "????????????";
                transfer.setMsg(msg);
                transferMapper.updateById(transfer);
                return;
            }
            String amount = null;
            if (jsonObject.getJSONObject("tokenTransferInfo") == null) {
                amount = jsonObject.getJSONArray("trc20ApprovalInfo").getJSONObject(0).getString("amount_str");
            } else {
                amount = jsonObject.getJSONObject("tokenTransferInfo").getString("amount_str");
            }
            if (amount == null) {
                msg = "??????????????????";
                transfer.setMsg(msg);
                transferMapper.updateById(transfer);
                redisTemplate.opsForValue().set("rechargeMsg:" + merchant.getRechargeAddress(), "??????????????????", 30, TimeUnit.MINUTES);

                return;
            }
            BigDecimal bigDecimal = new BigDecimal(amount).divide(new BigDecimal("1000000"));
            merchant.setBalance(merchant.getBalance().add(bigDecimal));
            merchantMapper.updateById(merchant);
            LogRecharge logRecharge = new LogRecharge();
            logRecharge.setUid(merchant.getId());
            logRecharge.setAmount(bigDecimal);
            logRecharge.setCreatedate(new Date());
            logRecharge.setTxId(transactionId);

            logRechargeMapper.insert(logRecharge);
        }
    }

    @Override
    public void replenishment(MerchantPayAddress merchantPayAddress) {
        merchantPayAddress.setGather(0);
        Merchant merchant = merchantMapper.selectById(merchantPayAddress.getMerchantId());


        BigDecimal bigDecimal = trxWallet.usdtBalanceOf(merchantPayAddress.getAddress());

        String userAddress = merchantPayAddress.getPayAddress();
        String md5 = SecureUtil.md5(merchant.getSign() + bigDecimal + merchantPayAddress.getPayAddress());

        if (merchantPayAddress.getStatus() == 3 || merchantPayAddress.getStatus() == 4) {
            BigDecimal fee = bigDecimal.multiply(merchant.getFee());
            if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
                log.error("??????????????????????????????????????????");
                merchantPayAddress.setStatus(3);
                merchantPayAddress.setMessage("??????????????????????????????????????????");
                sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getOrderAmount().toString(), "?????????????????????", "???????????????" + fee.toString() + "????????????" + merchant.getFee().toString(), merchantPayAddress.getAddress());

                merchantPayAddressMapper.updateById(merchantPayAddress);
                redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "??????????????????????????????????????????", 30, TimeUnit.MINUTES);
                return;
            }

            sendTelegramMessage(merchant, "?????????????????????", "?????????????????????", merchant.getBalance().toString(), "?????????????????????", "?????????????????????", "???????????????" + merchant.getBalance().toString() + "??????100USDT???????????????");



            merchant.setPresentedBalance(merchant.getPresentedBalance().subtract(fee));
            if (merchant.getPresentedBalance().compareTo(BigDecimal.ZERO) < 0) {
                if (merchant.getAgentId() != null) {
                    Agent agent = agentMapper.selectById(merchant.getAgentId());
                    if (agent != null) {

                        if (agent.getFee() != null) {
                            BigDecimal agentFee = bigDecimal.multiply(agent.getFee());
                            //??????
                            merchantPayAddress.setAgentFee(agentFee);
                            BigDecimal profit = agentFee.subtract(fee);
                            agent.setUnbalancedAmount(agent.getUnbalancedAmount().add(profit));
                            agentMapper.updateById(agent);
                        } else {
                            merchantPayAddress.setAgentFee(BigDecimal.ZERO);

                        }
                    } else {
                        merchantPayAddress.setAgentFee(BigDecimal.ZERO);
                    }
                } else {
                    merchantPayAddress.setAgentFee(BigDecimal.ZERO);
                }
            } else {
                merchantPayAddress.setAgentFee(fee);
            }
            merchantPayAddress.setFeeAmount(fee);
            merchant.setBalance(merchant.getBalance().subtract(fee));
            merchantMapper.updateById(merchant);


            MerchantCollectUseAddress collectUseAddress = merchantCollectUseAddressService.getOne(new QueryWrapper<MerchantCollectUseAddress>().eq("order_sn", merchantPayAddress.getOrderSn()));
            collectUseAddress.setAmount(collectUseAddress.getAmount().add(bigDecimal));
            merchantCollectUseAddressService.update(new UpdateWrapper<MerchantCollectUseAddress>().set("order_sn",null).set("amount",collectUseAddress.getAmount()).eq("id",collectUseAddress.getId()));

            redisTemplate.opsForValue().set("payMsg:" + merchantPayAddress.getAddress(), "SUCCESS", 30, TimeUnit.MINUTES);
            merchantPayAddress.setReturnMsg(merchantPayAddress.getReturnAddress() + "?amount=" + bigDecimal + "&sign=" + md5 + "&ownerAddress=" + userAddress + "&params=" + merchantPayAddress.getParams() + "&sysOrderNo=" + merchantPayAddress.getOrderSn());
        }
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("amount", merchantPayAddress.getUsdtBalance());
            jsonObject1.put("sign", md5);
            jsonObject1.put("ownerAddress", merchantPayAddress.getOwnerAddress());
            jsonObject1.put("orderNo", merchantPayAddress.getMerchantOrderNo());

            merchantPayAddress.setReturnRequestLog(jsonObject1.toJSONString());

            String returnMsg = HttpUtil.get(merchantPayAddress.getReturnAddress() + "?amount=" + bigDecimal + "&sign=" + md5 + "&ownerAddress=" + userAddress + "&orderNo=" + merchantPayAddress.getMerchantOrderNo() + "&params=" + merchantPayAddress.getParams() + "&sysOrderNo=" + merchantPayAddress.getOrderSn());
            merchantPayAddress.setReturnResponseLog(returnMsg);

            if (returnMsg != null && returnMsg.toUpperCase().equals("SUCCESS")) {
                merchantPayAddress.setSuccessTime(new Date());

                if (merchantPayAddress.getStatus() != 2) {
                    merchantPayAddress.setStatus(1);
                }
                merchantPayAddress.setMessage("SUCCESS");
            } else {
                merchantPayAddress.setStatus(5);
                merchantPayAddress.setMessage(returnMsg);
                sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getUsdtBalance().toString(), "??????????????????", returnMsg, userAddress);

            }
        } catch (Exception e) {
            merchantPayAddress.setStatus(5);
            merchantPayAddress.setMessage("??????????????????");
            sendTelegramMessage(merchant, merchantPayAddress.getMerchantOrderNo(), "????????????", merchantPayAddress.getUsdtBalance().toString(), "??????????????????", "??????????????????", userAddress);

        }


        merchantPayAddressMapper.updateById(merchantPayAddress);
    }

    @Override
    public void foGather(MerchantCollectUseAddress merchantCollectUseAddress,Merchant merchant) {
        //??????????????????10trx
        BigDecimal bigDecimal1 ;
        try {
             bigDecimal1 = trxWallet.usdtBalanceOf(merchantCollectUseAddress.getAddress());
        }catch (Exception e){
            return;
        }
        BigDecimal bigDecimal = trxWallet.balanceOfTron(merchantCollectUseAddress.getAddress());
        if (bigDecimal1 != null && bigDecimal1.compareTo(new BigDecimal("0.01")) > 0) {
            bigDecimal1 = bigDecimal1.subtract(new BigDecimal("0.01"));

            System.out.println("???????????????:" + bigDecimal1);
            if (bigDecimal.compareTo(BigDecimal.TEN) < 0) {
                trxWallet.sendTransaction(merchant.getTrxPrivateKey(), merchant.getTrxAddress(), merchantCollectUseAddress.getAddress(), BigDecimal.TEN, "TRX"+merchantCollectUseAddress.getAddress()+ DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS,new Date()));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //??????
            String s = trxWallet.usdtSendTransaction(merchantCollectUseAddress.getAddress(), merchantCollectUseAddress.getPrivateKey(), bigDecimal1, merchant.getUsdtAddress(), "USDT" + merchantCollectUseAddress.getAddress() + DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, new Date()));
            if ((!"402".equals(s))&&(!"401".equals(s))) {
                merchantCollectUseAddress.setAmount(BigDecimal.ZERO);
                merchantCollectUseAddress.setStatus(1);
                merchantCollectUseAddressService.updateById(merchantCollectUseAddress);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        bigDecimal = trxWallet.balanceOfTron(merchantCollectUseAddress.getAddress());

        if (bigDecimal == null || bigDecimal.compareTo(new BigDecimal("0.01")) <= 0) {

            return;
        }
        bigDecimal = bigDecimal.setScale(2, RoundingMode.DOWN);
        bigDecimal.subtract(new BigDecimal("0.01"));
        String s = trxWallet.sendTransaction(merchantCollectUseAddress.getPrivateKey(), merchantCollectUseAddress.getAddress(), merchant.getTrxAddress(), bigDecimal, merchantCollectUseAddress.getOrderSn());

    }


    public void sendTelegramMessage(Merchant merchant, String orderNo, String orderType, String amount, String
            title, String content, String address) {

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

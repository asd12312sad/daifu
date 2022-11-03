package com.ms.merchant.controller;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.*;
import com.ms.merchant.mapper.MerchantPayOrderAuditMapper;
import com.ms.merchant.mapper.MerchantPayOrderMapper;
import com.ms.merchant.mapper.MerchantPayUseAddressMapper;
import com.ms.merchant.mapper.MerchantUserMapper;
import com.ms.merchant.service.IMerchantPayAddressService;
import com.ms.merchant.service.IMerchantPayOrderAuditService;
import com.ms.merchant.service.MerchantService;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户代付记录Controller
 *
 * @author xiaobing
 * @date 2022-06-10
 */
@RestController
@RequestMapping("/payOrder")
public class MerchantPayOrderAuditController {
    @Autowired
    private IMerchantPayOrderAuditService merchantPayOrderAuditService;
    @Autowired
    private MerchantPayOrderMapper merchantPayOrderMapper;

    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;

    @Autowired
    private IMerchantPayAddressService merchantPayAddressService;
    @Autowired
    private MerchantService merchantService;


    @Autowired
    private TRXWallet trxWallet;

    @Autowired
    private MerchantUserMapper userMapper;

    /**
     * 大额记录
     */
    @MerchantApiNameAnnotation(apiName = "查询大额记录")
    @GetMapping("/wholesale/list")
    public R<IPage<MerchantPayOrder>> wholesaleList(MerchantPayOrder merchantPayOrder, @RequestParam Integer pageSize, @RequestParam Integer pageNumber) {

        IPage<MerchantPayOrder> page = new Page<>(pageNumber, pageSize);
        merchantPayOrder.setType(1);
        page = merchantPayOrderAuditService.selectPage(page, merchantPayOrder);
        for (MerchantPayOrder record : page.getRecords()) {
            record.setPrivateKey(null);
        }
        return R.success(page);
    }


    /**
     * 大额记录
     */
    @MerchantApiNameAnnotation(apiName = "查询代收异常记录")
    @GetMapping("/collection/list")
    public R<IPage<MerchantPayAddress>> CollectionPage(MerchantPayAddress merchantPayAddress, @RequestParam Integer pageSize, @RequestParam Integer pageNumber) {

        IPage<MerchantPayAddress> page = new Page<>(pageNumber, pageSize);
        merchantPayAddress.setType(3);
        page = merchantPayOrderAuditService.selectCollectionPage(page, merchantPayAddress);
        for (MerchantPayAddress record : page.getRecords()) {
            record.setPrivateKey(null);
            record.setHexAddress(null);
            record.setPrivateKeyBase(null);
        }
        return R.success(page);
    }

    /**
     * 审核代收订单
     */
    @MerchantApiNameAnnotation(apiName = "审核代收订单")
    @GetMapping("/collection/audit")
    public R<IPage<MerchantPayOrderAudit>> collectionAudit(@RequestParam String id, @RequestParam Integer status, @RequestParam String googleSign, String refuseMsg) {
        MerchantPayOrderAudit merchantPayOrderAudit = merchantPayOrderAuditService.getById(id);

        if (status == 1) {
            if (merchantPayOrderAudit == null) {
                return R.failed("订单不存在");
            }
            if (merchantPayOrderAudit.getStatus() != 0) {
                return R.failed("订单已审核");
            }
            if (merchantPayOrderAudit.getType() == 3) {
                return R.failed("订单类型不是代收");
            }
            MerchantPayAddress address = merchantPayAddressService.getOne(new QueryWrapper<MerchantPayAddress>().eq("merchant_id", merchantPayOrderAudit.getMerchantId()).eq("merchant_order_no", merchantPayOrderAudit.getMerchantOrderNo()));
            String returnMsg = HttpUtil.get(address.getReturnMsg());

            if (returnMsg != null && returnMsg.toUpperCase().equals("SUCCESS")) {
                address.setMessage("SUCCESS");
                merchantPayAddressService.updateById(address);
                merchantPayOrderAudit.setStatus(1);
                merchantPayOrderAuditService.updateById(merchantPayOrderAudit);
                return R.success("回调成功");
            } else {

                address.setMessage(returnMsg);
                return R.failed("回调失败 请检查您网站的API接口是否正常");
            }

        } else {
            merchantPayOrderAudit.setRefuseMsg(refuseMsg);
            merchantPayOrderAudit.setStatus(2);
            merchantPayOrderAuditService.updateById(merchantPayOrderAudit);
            return R.success();

        }

    }

    /**
     * 大额记录
     */
    @MerchantApiNameAnnotation(apiName = "查询异常记录")
    @GetMapping("/abnormity/list")
    public R<IPage<MerchantPayOrder>> abnormityList(MerchantPayOrder merchantPayOrder, @RequestParam Integer pageSize, @RequestParam Integer pageNumber) {

        IPage<MerchantPayOrder> page = new Page<>(pageNumber, pageSize);
        merchantPayOrder.setType(2);
        page = merchantPayOrderAuditService.selectPage(page, merchantPayOrder);
        for (MerchantPayOrder record : page.getRecords()) {
            record.setPrivateKey(null);
        }
        return R.success(page);
    }


    /**
     * 查询商户代付记录列表
     */
    @MerchantApiNameAnnotation(apiName = "审核代付订单")
    @GetMapping("/audit")
    public R<IPage<MerchantPayOrderAudit>> audit(@RequestParam String id, @RequestParam Integer status,  @RequestParam String googleSign, String refuseMsg) {

        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());
        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        MerchantPayOrderAudit merchantPayOrderAudit = merchantPayOrderAuditService.getById(id);

        if (merchantPayOrderAudit.getType() ==2){
            status = 2;
        }
        if (status == 1) {
            MerchantPayOrder merchantPayOrder = merchantPayOrderMapper.selectOne(new QueryWrapper<MerchantPayOrder>().eq("merchant_order_no", merchantPayOrderAudit.getMerchantOrderNo()).eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
            trxWallet.lockPayOrder(merchantPayOrder);

            if (merchantPayOrder.getStatus() != 4) {
                merchantPayOrderAudit.setStatus(1);
                merchantPayOrderAuditService.updateById(merchantPayOrderAudit);
                trxWallet.unLockPayOrder(merchantPayOrder.getId());
                return R.failed(401, "该订单已处理");
            }
            List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
            MerchantPayUseAddress useAddress = null;
            boolean trx = false;
            for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
                BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
                if (decimal != null && decimal.compareTo(merchantPayOrderAudit.getUsdtBalance()) >= 0) {
                    BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                    if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                        useAddress = merchantPayUseAddress;
                    } else {
                        trx = true;
                    }
                }
            }
            if (useAddress == null) {
                sendTelegramMessage(merchant, merchantPayOrderAudit.getMerchantOrderNo(), "代付订单", merchantPayOrderAudit.getUsdtBalance().toString(), "代付余额不足", "代付余额不足", merchant.getTrxAddress());
                if (trx) {
                    trxWallet.unLockPayOrder(merchantPayOrder.getId());
                    return R.failed(403, "代付地址TRX矿工费余额不足，请充值");
                } else {
                    trxWallet.unLockPayOrder(merchantPayOrder.getId());
                    return R.failed(403, "代付地址USDT余额不足，请充值");

                }
            }
            BigDecimal fee = merchant.getPayFee();
            if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
                sendTelegramMessage(merchant, merchantPayOrderAudit.getMerchantOrderNo(), "代付订单", merchantPayOrderAudit.getUsdtBalance().toString(), "手续费不足", "手续费不足", merchant.getTrxAddress());
                trxWallet.unLockPayOrder(merchantPayOrder.getId());
                return R.failed(898, "手续费不足");
            }
            Map<String, String> payMap = trxWallet.createPay(merchant, useAddress, merchantPayOrder);
            String msg = payMap.get("MSG");
            if (msg.equals("SUCCESS")) {
                merchantPayOrderAudit.setStatus(1);
                merchantPayOrderAuditService.updateById(merchantPayOrderAudit);
                trxWallet.unLockPayOrder(merchantPayOrder.getId());

                return R.success();
            } else if (msg.equals("订单加入队列等待中")) {
                merchantPayOrderAudit.setStatus(1);
                trxWallet.unLockPayOrder(merchantPayOrder.getId());
                merchantPayOrderAuditService.updateById(merchantPayOrderAudit);
                return R.failed("订单加入队列等待中");
            } else {
                trxWallet.unLockPayOrder(merchantPayOrder.getId());
                return R.failed(msg);
            }

        } else {

            MerchantPayOrder merchantPayOrder = merchantPayOrderMapper.selectOne(new QueryWrapper<MerchantPayOrder>().eq("merchant_order_no", merchantPayOrderAudit.getMerchantOrderNo()).eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
            boolean order = trxWallet.lockPayOrder(merchantPayOrder);
            if(!order){
                return R.failed("程序正在自动处理订单，请稍后重试");
            }
            merchantPayOrder.setStatus(0);
            merchantPayOrderMapper.updateById(merchantPayOrder);
            merchantPayOrderAudit.setRefuseMsg(refuseMsg);
            merchantPayOrderAudit.setStatus(2);
            merchantPayOrderAuditService.updateById(merchantPayOrderAudit);
            trxWallet.unLockPayOrder(merchantPayOrder.getId());
        }
        return R.success();
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


}

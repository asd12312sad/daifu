package com.ms.merchant.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.http.HttpUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.*;
import com.ms.merchant.mapper.MerchantPayUseAddressMapper;
import com.ms.merchant.mapper.MerchantUserMapper;
import com.ms.merchant.service.IMerchantPayOrderService;
import com.ms.merchant.service.MerchantPayUseAddressService;
import com.ms.merchant.service.MerchantService;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
public class MerchantPayOrderController {

    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;

    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TRXWallet trxWallet;

    @Autowired
    private MerchantUserMapper userMapper;

    /**
     * 查询商户代付记录列表
     */
    @MerchantApiNameAnnotation(apiName = "查询代付记录列表")
    @GetMapping("/list")
    public R<IPage<MerchantPayOrder>> list(MerchantPayOrder merchantPayOrder, @RequestParam Integer pageSize, @RequestParam Integer pageNumber) {

        IPage<MerchantPayOrder> page = new Page<>(pageNumber, pageSize);
        merchantPayOrder.setMerchantId(CommonRequestHolder.getCurrentUserId().toString());
        page = merchantPayOrderService.selectMerchantPayOrderList(page, merchantPayOrder);

        return R.success(page);
    }

    @MerchantApiNameAnnotation(apiName = "导出代付记录")
    @GetMapping("/export")
    public void export(MerchantPayOrder merchantPayOrder, HttpServletResponse response) {
        List<MerchantPayAddress> page = merchantPayOrderService.selectMerchantPayOrderExport(merchantPayOrder);
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("代付订单", "代付订单"),
                MerchantPayAddress.class, page);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("代付订单" + ".xlsx", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取商户代付记录详细信息
     */
    @MerchantApiNameAnnotation(apiName = "代付重新回调")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.success(merchantPayOrderService.getById(id));
    }



    /**
     * 获取商户代付记录详细信息
     */
    @MerchantApiNameAnnotation(apiName = "标记付款到账")
    @GetMapping(value = "markPayment")
    public R markPayment(@RequestParam("id") Long id) {
        MerchantPayOrder merchantPayOrder = merchantPayOrderService.getById(id);
        merchantPayOrder.setHaveUsdt(1);
        merchantPayOrder.setStatus(2);
        return R.success(merchantPayOrderService.updateById(merchantPayOrder));
    }
    /**
     * 获取商户代付记录详细信息
     */
    @MerchantApiNameAnnotation(apiName = "标记付款放弃")
    @GetMapping(value = "abandon")
    public R abandon(@RequestParam("id") Long id) {
        MerchantPayOrder merchantPayOrder = merchantPayOrderService.getById(id);
        merchantPayOrder.setHaveUsdt(0);
        merchantPayOrder.setStatus(0);
        return R.success(merchantPayOrderService.updateById(merchantPayOrder));
    }

    /**
     * 查询商户付款地址列表
     */
    @MerchantApiNameAnnotation(apiName = "代付修改金额")
    @GetMapping("/update/amount")
    public R updateAmount(String id, BigDecimal amount) {
        //重新支付
        MerchantPayOrder merchantPayOrder = merchantPayOrderService.getById(id);
        merchantPayOrder.setUsdtBalance(amount);
        return merchantPayOrderService.updateById(merchantPayOrder) ? R.success() : R.failed();
    }

    /**
     * 重新支付
     */
    @MerchantApiNameAnnotation(apiName = "代付重新支付")
//    @GetMapping("/real/pay")
    public R updateAmount(String id, String googleSign) {
//        HttpUtil.post("http://localhost:8080/api/pay/real/pay", "id=" + id + "&googleSign=" + googleSign);

        Merchant merchant = merchantService.getById(CommonRequestHolder.getCurrentUserId());
        MerchantUser merchantUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSign, merchantUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }

        //重新支付
        MerchantPayOrder merchantPayOrder = merchantPayOrderService.getById(id);

        if (merchantPayOrder.getHaveUsdt() == 1) {
            return R.failed(401, "订单已支付");
        }
        if ("订单已存在".equals(merchantPayOrder.getPayResult())) {
            return R.failed(401, "重复订单 不可重新支付");
        }
//        if (merchantPayOrder.getStatus() != 0) {
//            return R.failed(401, "该订单已处理");
//        }
        List<MerchantPayUseAddress> useAddressList = merchantPayUseAddressMapper.selectList(new QueryWrapper<MerchantPayUseAddress>().eq("merchant_id", CommonRequestHolder.getCurrentUserId()));
        MerchantPayUseAddress useAddress = null;
        boolean trx = false;
        for (MerchantPayUseAddress merchantPayUseAddress : useAddressList) {
            BigDecimal decimal = trxWallet.usdtBalanceOf(merchantPayUseAddress.getAddress());
            if (decimal != null && decimal.compareTo(merchantPayOrder.getUsdtBalance()) >= 0) {
                BigDecimal trcBalance = trxWallet.balanceOfTron(merchantPayUseAddress.getAddress());
                if (trcBalance != null && trcBalance.compareTo(new BigDecimal("10")) > 0) {
                    useAddress = merchantPayUseAddress;
                } else {
                    trx = true;
                }
            }
        }

        if (useAddress == null) {
            sendTelegramMessage(merchant, merchantPayOrder.getMerchantOrderNo(), "代付订单", merchantPayOrder.getUsdtBalance().toString(), "代付余额不足", "代付余额不足", merchant.getTrxAddress());
            if (trx) {
                return R.failed(403, "代付地址TRX矿工费余额不足，请充值");
            } else {
                return R.failed(403, "代付地址USDT余额不足，请充值");
            }
        }
        BigDecimal fee = merchant.getPayFee();
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            sendTelegramMessage(merchant, merchantPayOrder.getMerchantOrderNo(), "代付订单", merchantPayOrder.getUsdtBalance().toString(), "手续费不足", "手续费不足", merchant.getTrxAddress());
            return R.failed(898, "手续费不足");
        }
        Map<String, String> payMap = trxWallet.createPay(merchant, useAddress, merchantPayOrder);
        String msg = payMap.get("MSG");
        if (msg.equals("SUCCESS")) {

            return R.success();
        } else if (msg.equals("订单加入队列等待中")) {
            return R.failed("订单加入队列等待中");
        } else {
            return R.failed(msg);
        }

    }


    /**
     * 查询商户付款地址列表
     */
    @MerchantApiNameAnnotation(apiName = "代付重新回调")
    @GetMapping("/huidiao")
    public R huidiao(String id) {
        MerchantPayOrder address = merchantPayOrderService.getById(id);
        String returnMsg = HttpUtil.get(address.getReturnMsg());
        if (returnMsg != null && returnMsg.toUpperCase().equals("SUCCESS")) {
            address.setMessage("回调成功");
        } else {
            address.setMessage("回调失败 请检查您网站的API接口是否正常");
        }
        boolean b = merchantPayOrderService.updateById(address);
        return b ? R.success() : R.failed();
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

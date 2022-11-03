package com.ms.merchant.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.*;
import com.ms.merchant.mapper.*;
import com.ms.merchant.service.IMerchantPayAddressService;
import com.ms.merchant.service.impl.GoogleAuthenticator;
import com.ms.merchant.usdttransfer.service.TRXWallet;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;


/**
 * 商户付款地址Controller
 *
 * @author ruoyi
 * @date 2022-06-03
 */
@RestController
@RequestMapping("/system/address")
public class MerchantPayAddressController {
    @Autowired
    private IMerchantPayAddressService merchantPayAddressService;

    @Autowired
    private TRXWallet trxWallet;
    @Autowired
    private MerchantUserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private MerchantPayOrderAuditMapper merchantPayOrderAuditMapper;

    @Autowired
    private MerchantCollectUseAddressMapper merchantCollectUseAddressMapper;

    /**
     * 查询商户付款地址列表
     */
    @PreAuthorize("@ss.hasPermi('system:address:list')")
    @GetMapping("/collect/list")
    @MerchantApiNameAnnotation(apiName = "查询代收收款地址列表")
    public R collectList( @RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        IPage<MerchantCollectUseAddress> page = new Page<>(pageNumber, pageSize);
        page = merchantCollectUseAddressMapper.selectPage(page,new QueryWrapper<MerchantCollectUseAddress>().eq("merchant_id",CommonRequestHolder.getCurrentUserId().toString()).orderByDesc("amount"));
        for (MerchantCollectUseAddress payAddress : page.getRecords()) {
            payAddress.setPrivateKey(null);
            payAddress.setPrivateKeyBase(null);
            payAddress.setUsdtBalance(trxWallet.usdtBalanceOf(payAddress.getAddress()));
            payAddress.setHexAddress(null);
        }
        return R.success(page);
    }




    /**
     * 手动归集
     */
    @PreAuthorize("@ss.hasPermi('system:address:list')")
    @GetMapping("/collect/notionalPooling")
    @MerchantApiNameAnnotation(apiName = "手动归集")
    public R notionalPooling( @RequestParam String id) {
        MerchantCollectUseAddress   payAddress = merchantCollectUseAddressMapper.selectById(id);
        if (payAddress.getStatus()==0){
            return R.failed("在用地址不可手动归集");
        }

        BigDecimal bigDecimal = trxWallet.usdtBalanceOf(payAddress.getAddress());

        BigDecimal bigDecimal1 = trxWallet.balanceOfTron(payAddress.getAddress());
        if (bigDecimal.compareTo(BigDecimal.ZERO)==0 && bigDecimal1.compareTo(BigDecimal.ZERO)==0){
            payAddress.setAmount(bigDecimal);
            merchantCollectUseAddressMapper.updateById(payAddress);
            return R.failed("无需归集");
        }

        Merchant merchant = merchantMapper.selectById(CommonRequestHolder.getCurrentUserId());
        if (bigDecimal1.compareTo(BigDecimal.TEN)<0) {
            trxWallet.sendTransaction(merchant.getTrxPrivateKey(),merchant.getTrxAddress(),  payAddress.getAddress(), BigDecimal.TEN, payAddress.getAddress());
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        trxWallet.usdtSendTransaction(payAddress.getAddress(),payAddress.getPrivateKey(),bigDecimal,merchant.getUsdtAddress(),payAddress.getAddress());

        BigDecimal bigDecimal2 = trxWallet.balanceOfTron(payAddress.getAddress());

        trxWallet.sendTransaction(payAddress.getPrivateKey(), payAddress.getAddress(),merchant.getTrxAddress(), bigDecimal2, payAddress.getAddress());
        return R.success("归集成功");
    }



    /**
     * 查询商户付款地址列表
     */
    @PreAuthorize("@ss.hasPermi('system:address:list')")
    @GetMapping("/list")
    @MerchantApiNameAnnotation(apiName = "查询代收订单列表")
    public R list(MerchantPayAddress merchantPayAddress, @RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        IPage<MerchantPayAddress> page = new Page<>(pageNumber, pageSize);
        merchantPayAddress.setMerchantId(CommonRequestHolder.getCurrentUserId().toString());
        page = merchantPayAddressService.selectMerchantPayAddressList(page, merchantPayAddress);
        for (MerchantPayAddress payAddress : page.getRecords()) {
            payAddress.setPrivateKey(null);
            payAddress.setPrivateKeyBase(null);
            payAddress.setHexAddress(null);
        }

        return R.success(page);
    }

    /**
     * 查询商户付款地址列表
     */
    @MerchantApiNameAnnotation(apiName = "导出代收订单")
    @GetMapping("/export")
    public void export(MerchantPayAddress merchantPayAddress, HttpServletResponse response) {
        List<MerchantPayAddress> page = merchantPayAddressService.selectMerchantPayAddressExport(merchantPayAddress);
        for (MerchantPayAddress payAddress : page) {
            BigDecimal trx = trxWallet.balanceOfTron(payAddress.getAddress());
            payAddress.setTrxBalance(trx);
            BigDecimal usdtBalanceOf = trxWallet.usdtBalanceOf(payAddress.getAddress());
            payAddress.setUsdtBalance(usdtBalanceOf);
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("代收订单", "代收订单"),
                MerchantPayAddress.class, page);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("代收订单" + "." + "xls", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
        }
    }


    /**
     * 查询商户付款地址列表
     */
    @MerchantApiNameAnnotation(apiName = "代收重新回调")
    @GetMapping("/huidiao")
    public R huidiao(String id) {
        MerchantPayAddress address = merchantPayAddressService.getById(id);
        String returnMsg = HttpUtil.get(address.getReturnMsg());
        if (returnMsg != null && returnMsg.toUpperCase().equals("SUCCESS")) {
            address.setMessage("SUCCESS");
            merchantPayAddressService.updateById(address);
            return R.success("回调成功");
        } else {
            address.setMessage(returnMsg);
            return R.failed("回调失败 请检查您网站的API接口是否正常");
        }
    }

    /**
     * 查询商户付款地址列表
     */
    @MerchantApiNameAnnotation(apiName = "代收补单")
    @GetMapping("/budan")
    public R budan(@RequestParam String googleSignCode, @RequestParam String id, @RequestParam BigDecimal bigDecimal, @RequestParam String userAddress) {
        MerchantPayAddress merchantPayAddress = merchantPayAddressService.getById(id);
        MerchantUser loginUser = userMapper.selectById(CommonRequestHolder.getCurrentDetailUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSignCode, loginUser.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }


        String msg = "";
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
            msg = "付款金额与订单金额不匹配，自动补单";
            merchantPayAddress.setStatus(2);
            merchantPayAddress.setMessage("付款金额与订单金额不匹配，自动补单");
        }


        BigDecimal trxBalance = trxWallet.balanceOfTron(merchant.getTrxAddress());

        if (trxBalance.compareTo(BigDecimal.TEN) < 0) {
            merchantPayAddress.setStatus(4);
            merchantPayAddress.setMessage("商户TRX手续费不足无法完成归集,请联系商户处理");

            merchantPayAddressService.updateById(merchantPayAddress);
            return R.failed("TRX手续费不足无法完成归集");
        }


        BigDecimal fee = bigDecimal.multiply(merchant.getFee());
        if (merchant.getBalance() == null || fee.compareTo(merchant.getBalance()) > 0) {
            merchantPayAddress.setStatus(3);
            merchantPayAddress.setMessage("商户手续费不足请联系商户处理");
            merchantPayAddressService.updateById(merchantPayAddress);
            return R.failed("手续费不足");
        }
        //从总账户转入10trx
        trxWallet.sendTransaction( merchant.getTrxPrivateKey(),merchant.getTrxAddress(), merchantPayAddress.getAddress(), BigDecimal.TEN, merchantPayAddress.getOrderSn());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        merchant.setPresentedBalance(merchant.getPresentedBalance().subtract(fee));
        if (merchant.getPresentedBalance().compareTo(BigDecimal.ZERO) < 0) {
            if (merchant.getAgentId() != null) {
                Agent agent = agentMapper.selectById(merchant.getAgentId());
                if (agent != null) {

                    if (agent.getFee() != null) {
                        BigDecimal agentFee = bigDecimal.multiply(agent.getFee());
                        //利润
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

        merchantPayAddress.setStatus(1);
        merchantPayAddress.setHaveUsdt(1);


        merchantMapper.updateById(merchant);

        trxBalance = trxWallet.balanceOfTron(merchantPayAddress.getAddress());

        //归集
        String result = trxWallet.usdtSendTransaction(merchantPayAddress.getAddress(), merchantPayAddress.getPrivateKey(), bigDecimal, merchant.getUsdtAddress(), merchantPayAddress.getOrderSn());
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
                merchantPayAddressService.updateById(merchantPayAddress);

                return R.success();
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
                merchantPayOrderAuditMapper.insert(merchantPayOrderAudit);

                merchantPayAddress.setStatus(5);
                merchantPayAddress.setMessage(returnMsg);
                merchantPayAddressService.updateById(merchantPayAddress);

                return R.failed("回调信息" + returnMsg);

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
            merchantPayOrderAuditMapper.insert(merchantPayOrderAudit);
            merchantPayAddress.setStatus(5);
            merchantPayAddress.setMessage("回调网络异常");
            merchantPayAddressService.updateById(merchantPayAddress);
            return R.failed("回调网络异常");
        }

    }

    /**
     * 获取商户付款地址详细信息
     */
    @MerchantApiNameAnnotation(apiName = "获取商户代收订单详细信息")
    @GetMapping(value = "/{address}")
    public R getInfo(@PathVariable("address") String address) {
        return R.success(merchantPayAddressService.getById(address));
    }


}

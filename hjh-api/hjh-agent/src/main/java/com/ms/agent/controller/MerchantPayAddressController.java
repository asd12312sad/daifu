package com.ms.agent.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.agent.domain.MerchantPayAddress;
import com.ms.agent.service.IMerchantPayAddressService;
import com.ms.agent.service.impl.TRXWallet;
import com.ms.common.core.annotation.MerchantApiNameAnnotation;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
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

    /**
     * 查询商户付款地址列表
     */
    @PreAuthorize("@ss.hasPermi('system:address:list')")
    @GetMapping("/list")
    public R list(MerchantPayAddress merchantPayAddress, @RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        IPage<MerchantPayAddress> page = new Page<>(pageNumber, pageSize);
        merchantPayAddress.setAgentId(CommonRequestHolder.getCurrentUserId().intValue());
        page = merchantPayAddressService.selectMerchantPayAddressList(page, merchantPayAddress);
        for (MerchantPayAddress payAddress : page.getRecords()) {
            BigDecimal trx = trxWallet.balanceOfTron(payAddress.getAddress());
            payAddress.setTrxBalance(trx);
            BigDecimal usdtBalanceOf = trxWallet.usdtBalanceOf(payAddress.getAddress());
            payAddress.setUsdtBalance(usdtBalanceOf);

            payAddress.setPrivateKey(null);
            payAddress.setPrivateKeyBase(null);
            payAddress.setHexAddress(null);
        }

        return R.success(page);
    }

    /**
     * 查询商户付款地址列表
     */
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
     * 获取商户付款地址详细信息
     */
    @GetMapping(value = "/{address}")
    public R getInfo(@PathVariable("address") String address) {
        return R.success(merchantPayAddressService.getById(address));
    }


}

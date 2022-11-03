package com.hjh.erp.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.http.HttpUtil;
import com.hjh.common.core.domain.entity.SysUser;
import com.hjh.erp.domain.MerchantPayAddress;
import com.hjh.erp.service.IMerchantPayAddressService;
import com.hjh.common.core.page.TableDataInfo;
import com.hjh.common.utils.poi.ExcelUtil;
import com.hjh.system.mapper.SysUserMapper;
import com.hjh.usdttransfer.service.TRXWallet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hjh.common.annotation.Log;
import com.hjh.common.core.controller.BaseController;
import com.hjh.common.core.domain.AjaxResult;
import com.hjh.common.enums.BusinessType;

/**
 * 商户付款地址Controller
 *
 * @author ruoyi
 * @date 2022-06-03
 */
@RestController
@RequestMapping("/system/address")
public class MerchantPayAddressController extends BaseController {
    @Autowired
    private IMerchantPayAddressService merchantPayAddressService;

    @Autowired
    private TRXWallet trxWallet;

    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 查询商户付款地址列表
     */
    @PreAuthorize("@ss.hasPermi('system:address:list')")
    @GetMapping("/list")
    public TableDataInfo list(MerchantPayAddress merchantPayAddress) {
        startPage();
        List<MerchantPayAddress> page = merchantPayAddressService.selectMerchantPayAddressList(merchantPayAddress);
        for (MerchantPayAddress payAddress : page) {
            BigDecimal trx=trxWallet.balanceOfTron(payAddress.getAddress());
            payAddress.setTrxBalance(trx.toString());
            BigDecimal usdtBalanceOf=trxWallet.usdtBalanceOf(payAddress.getAddress());
            payAddress.setUsdtBalance(usdtBalanceOf.toString());
        }
        return getDataTable(page);
    }




    /**
     * 查询商户付款地址列表
     */
//    @PreAuthorize("@ss.hasPermi('system:address:list')")
//    @GetMapping("/huidiao")
    public AjaxResult huidiao(String  id,String adminGoogleSign) {
        SysUser sysUser = sysUserMapper.selectUserById(super.getUserId());
        if (!isGoogle(adminGoogleSign,sysUser.getGoogleSignCode())){
            return AjaxResult.error("谷歌验证码错误");
        }
        MerchantPayAddress address = merchantPayAddressService.getById(id);
        String returnMsg = HttpUtil.get(address.getReturnMsg());
        if(returnMsg!= null && returnMsg.toUpperCase().equals("SUCCESS")){
            address.setMessage("SUCCESS");
        }else {
            address.setMessage(returnMsg);
        }
       return toAjax(merchantPayAddressService.updateById(address));
    }

    /**
     * 导出商户付款地址列表
     */
    @PreAuthorize("@ss.hasPermi('system:address:export')")
    @Log(title = "商户付款地址" , businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(MerchantPayAddress merchantPayAddress) {
        List<MerchantPayAddress> list = merchantPayAddressService.selectMerchantPayAddressList(merchantPayAddress);
        ExcelUtil<MerchantPayAddress> util = new ExcelUtil<MerchantPayAddress>(MerchantPayAddress. class);
        return util.exportExcel(list, "商户付款地址数据");
    }

    /**
     * 获取商户付款地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:address:query')")
    @GetMapping(value = "/{address}")
    public AjaxResult getInfo(@PathVariable("address") String address) {
        return AjaxResult.success(merchantPayAddressService.getById(address));
    }

    /**
     * 新增商户付款地址
     */
//    @PreAuthorize("@ss.hasPermi('system:address:add')")
//    @Log(title = "商户付款地址" , businessType = BusinessType.INSERT)
//    @PostMapping
    public AjaxResult add(@RequestBody MerchantPayAddress merchantPayAddress) {
        return toAjax(merchantPayAddressService.save(merchantPayAddress));
    }

    /**
     * 修改商户付款地址
     */
//    @PreAuthorize("@ss.hasPermi('system:address:edit')")
//    @Log(title = "商户付款地址" , businessType = BusinessType.UPDATE)
//    @PutMapping
    public AjaxResult edit(@RequestBody MerchantPayAddress merchantPayAddress) {
        return AjaxResult.toAjax(merchantPayAddressService.updateById(merchantPayAddress));
    }

    /**
     * 删除商户付款地址
     */
//    @PreAuthorize("@ss.hasPermi('system:address:remove')")
//    @Log(title = "商户付款地址" , businessType = BusinessType.DELETE)
//    @DeleteMapping("/{addresss}")
    public AjaxResult remove(@PathVariable String[] addresss) {
        List<String> idList = new ArrayList<>(addresss.length);
        Collections.addAll(idList, addresss);
        return AjaxResult.toAjax(merchantPayAddressService.removeByIds(idList));
    }
}

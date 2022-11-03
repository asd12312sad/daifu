package com.ms.merchant.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.core.exception.BaseException;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.Merchant;
import com.ms.merchant.domain.MerchantPayUseAddress;
import com.ms.merchant.mapper.MerchantMapper;
import com.ms.merchant.service.MerchantPayUseAddressService;
import com.ms.merchant.service.MerchantService;
import com.ms.merchant.usdttransfer.service.TRXWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService
{
    @Autowired
    private MerchantPayUseAddressService merchantPayUseAddressService;

    private static SecureRandom random = new SecureRandom();

    @Autowired
    private TRXWallet trxWallet;


    @Override
    public Object test() {
        Merchant merchant = this.baseMapper.selectById(CommonRequestHolder.getCurrentUserId());

        String orderNo = System.currentTimeMillis()/1000+""+merchant.getId()+"";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo);
        String json = HttpUtil.post("https://gateway.paycloud.pro/ucc/pay/createAddress?merchantId="+merchant.getId()+"&sign="+md5+"&orderNo="+orderNo+"&returnAddress="+merchant.getReturnAddress()+"&amount=1","{}");
        log.error(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            if ("TRC_BALANCE_ERROR".equals(msg)){
                throw new BaseException("TRC手续费余额不足");
            }
        }

        String address = JSON.parseObject(json).getJSONObject("data").getString("address");
        return R.success(jsonObject.getJSONObject("data"));


//        Map<String,String> map = new HashMap<>();
//        map.put("address",address);
//        return map;
    }

    @Override
    public Object testPay(String address,BigDecimal amount) {
        Merchant merchant = this.baseMapper.selectById(CommonRequestHolder.getCurrentUserId());

        String orderNo = System.currentTimeMillis()/1000+""+merchant.getId()+"";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo+address);
        String json = HttpUtil.get("https://api.adminuu.pro/ucc/pay/createPay?merchantId="+merchant.getId()+"&sign="+md5+"&orderNo="+orderNo+"&payAddress="+address+"&amount="+amount+"&returnAddress="+merchant.getReturnPayAddress());
        log.error(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            if ("TRC_BALANCE_ERROR".equals(msg)){
                throw new BaseException("TRC手续费余额不足");
            }
            if ("USDT_BALANCE_ERROR".equals(msg)){
                throw new BaseException("USDT余额不足");
            }
            if ("FEE_BALANCE_ERROR".equals(msg)){
                throw new BaseException("手续费余额不足");
            }

        }

        String MSG = JSON.parseObject(json).getJSONObject("data").getString("MSG");
        Map<String,String> map = new HashMap<>();
        if (MSG.equals("SUCCESS")) {
            String orderId = JSON.parseObject(json).getJSONObject("data").getString("ORDER_NO");
            map.put("orderId",orderId);
        }
        return map;
    }

    @Override
    public void createPayWallet(Merchant merchant) {
        MerchantPayUseAddress merchantPayUseAddress = new MerchantPayUseAddress();
        merchantPayUseAddress.setMerchantId(merchant.getId());
        JSONObject eCkey = TRXWallet.createAddress();
        String privateKey = eCkey.getString("apiKey");

        merchantPayUseAddress.setAddress(eCkey.getString("address"));
        merchantPayUseAddress.setPrivateKey(privateKey);
        merchantPayUseAddress.setHexAddress(privateKey);
        merchantPayUseAddress.setPrivateKeyBase(privateKey);
        merchantPayUseAddress.setCreateTime(new Date());
        merchantPayUseAddress.setUpdateTime(new Date());

        merchantPayUseAddressService.save(merchantPayUseAddress);
    }


}

package com.ms.agent.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.agent.domain.Merchant;
import com.ms.agent.domain.MerchantPayUseAddress;
import com.ms.agent.mapper.MerchantMapper;
import com.ms.agent.mapper.MerchantPayUseAddressMapper;
import com.ms.agent.service.MerchantService;
import com.ms.common.core.exception.BaseException;
import com.ms.common.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;
    private static SecureRandom random = new SecureRandom();

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
        merchantPayUseAddressMapper.insert(merchantPayUseAddress);
    }

    @Override
    public Object test(Long merchantId) {
        Merchant merchant = this.baseMapper.selectById(merchantId);

        String orderNo = System.currentTimeMillis() / 1000 + "" + merchant.getId() + "";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo);


        String json = HttpUtil.post("https://api.adminuu.pro/ucc/pay/createAddress?merchantId=" + merchant.getId() + "&sign=" + md5 + "&orderNo=" + orderNo + "&returnAddress=" + merchant.getReturnAddress() + "&amount=1", "{}");
        log.error(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            if ("TRC_BALANCE_ERROR".equals(msg)) {
                throw new BaseException("TRC手续费余额不足");
            }
        }

        String address = JSON.parseObject(json).getJSONObject("data").getString("address");
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        return R.success(jsonObject.getJSONObject("data"));

//        return map;
    }

    @Override
    public Object testPay(String address, BigDecimal amount, Long merchantId) {
        Merchant merchant = this.baseMapper.selectById(merchantId);

        String orderNo = System.currentTimeMillis() / 1000 + "" + merchant.getId() + "";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo + address);
        String json = HttpUtil.get("https://api.adminuu.pro/ucc/pay/createPay?merchantId=" + merchant.getId() + "&sign=" + md5 + "&orderNo=" + orderNo + "&payAddress=" + address + "&amount=" + amount + "&returnAddress=" + merchant.getReturnPayAddress());
        log.error(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            if ("TRC_BALANCE_ERROR".equals(msg)) {
                throw new BaseException("TRC手续费余额不足");
            }
            if ("USDT_BALANCE_ERROR".equals(msg)) {
                throw new BaseException("USDT余额不足");
            }
            if ("FEE_BALANCE_ERROR".equals(msg)) {
                throw new BaseException("手续费余额不足");
            }
            if ("PAY_ADDRESS_ERROR".equals(msg)) {
                throw new BaseException("支付地址错误");
            }
            throw new BaseException(msg);
        }

        Map<String, String> map = new HashMap<>();
        String orderId = JSON.parseObject(json).getJSONObject("data").getString("ORDER_NO");
        map.put("orderId", orderId);
        return map;
    }


}

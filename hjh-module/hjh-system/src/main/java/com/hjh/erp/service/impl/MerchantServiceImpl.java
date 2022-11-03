package com.hjh.erp.service.impl;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjh.common.exception.BaseException;
import com.hjh.erp.mapper.MerchantPayUseAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.MerchantMapper;
import com.hjh.erp.domain.Merchant;
import com.hjh.erp.service.IMerchantService;
import org.tron.common.crypto.ECKey;

/**
 * 商户管理Service业务层处理
 *
 * @author xiaobing
 * @date 2022-06-02
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper,Merchant> implements IMerchantService
{


    @Autowired
    private MerchantPayUseAddressMapper merchantPayUseAddressMapper;
    private static SecureRandom random = new SecureRandom();


    /**
     * 查询商户管理列表
     *
     * @param merchant 商户管理
     * @return 商户管理集合
     */
    @Override
    public List<Merchant> selectMerchantList(Merchant merchant){
        if (merchant.getParams() == null){
            merchant.setParams(new HashMap<>());
        }
       return  this.baseMapper.selectMerchantList(merchant,merchant.getParams());

    }


    @Override
    public Object test(Long id) {
        Merchant merchant = this.baseMapper.selectById(id);

        String orderNo = System.currentTimeMillis()/1000+""+merchant.getId()+"";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo);


        String json = HttpUtil.post("https://api.adminuu.pro/ucc/pay/createAddress?merchantId="+merchant.getId()+"&sign="+md5+"&orderNo="+orderNo+"&returnAddress=https://api.adminuu.pro/admin/erp/merchant/return/"+merchant.getId(),"{}");
        log.error(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            if ("TRC_BALANCE_ERROR".equals(msg)){
                throw new BaseException("TRC手续费余额不足");
            }
        }

        String address = JSON.parseObject(json).getJSONObject("data").getString("address");
        Map<String,String> map = new HashMap<>();
        map.put("address",address);
        return map;
    }

    @Override
    public Object aaaaaaa(String ownerAddress) {
        Merchant merchant = this.baseMapper.selectById(12);

        String orderNo = System.currentTimeMillis()/1000+""+merchant.getId()+"";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo);


        String json = HttpUtil.post("https://api.adminuu.pro/ucc/pay/createAddress?merchantId="+merchant.getId()+"&sign="+md5+"&orderNo="+orderNo+"&ownerAddress="+ownerAddress+"&returnAddress=https://api.adminuu.pro/admin/erp/merchant/return/"+merchant.getId(),"{}");
        log.error(json);
        String address = JSON.parseObject(json).getJSONObject("data").getString("address");
        Map<String,String> map = new HashMap<>();
        map.put("address",address);
        return map;
    }

    @Override
    public Object testPay(Long id,String address,BigDecimal amount) {
        Merchant merchant = this.baseMapper.selectById(id);

        String orderNo = System.currentTimeMillis()/1000+""+merchant.getId()+"";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo+address);
        String json = HttpUtil.get("https://api.adminuu.pro/ucc/pay/createPay?merchantId="+merchant.getId()+"&sign="+md5+"&orderNo="+orderNo+"&payAddress="+address+"&amount="+amount+"&returnAddress=https://api.adminuu.pro/admin/erp/merchant/return/"+merchant.getId());
        log.error(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getInteger("code") != 200) {
            String msg = jsonObject.getString("msg");
            if ("TRC_BALANCE_ERROR".equals(msg)){
                throw new BaseException("TRC手续费余额不足");
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
    public Object bbbbbbb(String payAddress,String address, BigDecimal amount) {
        Merchant merchant = this.baseMapper.selectById(15);
        String orderNo = System.currentTimeMillis()/1000+""+merchant.getId()+"";
        String md5 = SecureUtil.md5(merchant.getSign() + orderNo+address);
        String json = HttpUtil.get("https://api.adminuu.pro/ucc/pay/createPay?merchantId="+merchant.getId()+"&merchantAddress="+payAddress+"&sign="+md5+"&orderNo="+orderNo+"&payAddress="+address+"&amount="+amount+"&returnAddress=https://api.adminuu.pro/admin/erp/merchant/return/"+merchant.getId());
        log.error(json);
        String MSG = JSON.parseObject(json).getJSONObject("data").getString("MSG");

        Map<String,String> map = new HashMap<>();
        if (MSG.equals("SUCCESS")) {
            String orderId = JSON.parseObject(json).getJSONObject("data").getString("ORDER_NO");
            map.put("orderId",orderId);
        }
        return map;
    }

//    @Override
//    public void createPayWallet(Merchant merchant) {
//        MerchantPayUseAddress merchantPayUseAddress = new MerchantPayUseAddress();
//        merchantPayUseAddress.setMerchantId(merchant.getId());
//        ECKey eCkey = new ECKey(random);
//        String privateKey = ByteArray.toHexString(eCkey.getPrivKeyBytes());
//        byte[] addressBytes = eCkey.getAddress();
//        String hexAddress = ByteArray.toHexString(addressBytes);
//        Map<String, String> addressInfo = new HashMap<>(3);
//        addressInfo.put("address", TRXWallet.toViewAddress(hexAddress));
//        String privateKeyBase58 = TRXWallet.encode58Check(eCkey.getPrivKeyBytes());
//        merchantPayUseAddress.setAddress(TRXWallet.toViewAddress(hexAddress));
//        merchantPayUseAddress.setPrivateKey(privateKey);
//        merchantPayUseAddress.setHexAddress(hexAddress);
//        merchantPayUseAddress.setPrivateKeyBase(privateKeyBase58);
//        merchantPayUseAddress.setCreateTime(new Date());
//        merchantPayUseAddress.setUpdateTime(new Date());
//
//        merchantPayUseAddressMapper.insert(merchantPayUseAddress);
//    }
}

package com.hjh.erp.service;

import com.hjh.erp.domain.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;


/**
 * 商户管理Service接口
 *
 * @author xiaobing
 * @date 2022-06-02
 */
public interface IMerchantService  extends IService<Merchant>
{

    /**
     * 查询商户管理列表
     *
     * @param merchant 商户管理
     * @return 商户管理集合
     */
    public List<Merchant> selectMerchantList(Merchant merchant);

    Object test(Long id);

    Object aaaaaaa(String address);

    Object testPay(Long id,String address,BigDecimal amount);

    Object bbbbbbb(String payAddress,String address, BigDecimal amount);

//    void createPayWallet(Merchant merchant);

}

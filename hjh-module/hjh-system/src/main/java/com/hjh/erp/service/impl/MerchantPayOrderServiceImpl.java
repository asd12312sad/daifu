package com.hjh.erp.service.impl;

import java.util.*;

import cn.hutool.core.date.DateUtil;
import com.hjh.common.utils.DateUtils;
import com.hjh.erp.domain.HomeCountVO;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.MerchantPayOrderMapper;
import com.hjh.erp.domain.MerchantPayOrder;
import com.hjh.erp.service.IMerchantPayOrderService;

/**
 * 商户代付记录Service业务层处理
 *
 * @author xiaobing
 * @date 2022-06-10
 */
@Service
public class MerchantPayOrderServiceImpl extends ServiceImpl<MerchantPayOrderMapper,MerchantPayOrder> implements IMerchantPayOrderService
{



    /**
     * 查询商户代付记录列表
     *
     * @param merchantPayOrder 商户代付记录
     * @return 商户代付记录集合
     */
    @Override
    public List<MerchantPayOrder> selectMerchantPayOrderList(MerchantPayOrder merchantPayOrder){
        if (merchantPayOrder.getParams() == null){
            merchantPayOrder.setParams(new HashMap<>());
        }
       return  this.baseMapper.selectMerchantPayOrderList(merchantPayOrder,merchantPayOrder.getParams());

    }

    @Override
    public HomeCountVO selectHomeCount() {
        HomeCountVO homeCountVO = this.baseMapper.selectHomeCollectCount();

        HomeCountVO homeCountVO1 = this.baseMapper.selectHomePayCount();
        homeCountVO.setPayCount(homeCountVO1.getPayCount());
        homeCountVO.setPayEndCount(homeCountVO1.getPayEndCount());
        homeCountVO.setPayReturnCount(homeCountVO1.getPayReturnCount());
        homeCountVO.setPaySumAmount(homeCountVO1.getPaySumAmount());
        return homeCountVO;
    }

    @Override
    public Map<String,Object> weekSum() {
        List<String> dates = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            dates.add(DateUtil.format(DateUtils.addDays(new Date(), -i),"yyyy-MM-dd"));
        }

        List<String> dateList = new ArrayList<>();

        List<Integer> payCountList = new ArrayList<>();
        List<Integer> payEndCountList = new ArrayList<>();
        List<Integer> PayReturnCountList = new ArrayList<>();
        List<Integer> collectCountList = new ArrayList<>();
        List<Integer> collectEndCountList = new ArrayList<>();
        List<Integer> collectReturnCountList = new ArrayList<>();

        for (String date : dates) {
            HomeCountVO pay =  this.baseMapper.selectHomeCountPay(date);
            HomeCountVO collect =  this.baseMapper.selectHomeCountCollect(date);
            dateList.add(date);
            payCountList.add(pay.getPayCount());
            payEndCountList.add(pay.getPayEndCount());
            PayReturnCountList.add(pay.getPayReturnCount());
            collectCountList.add(collect.getCollectCount());
            collectEndCountList.add(collect.getCollectEndCount());
            collectReturnCountList.add(collect.getCollectReturnCount());
        }

        Map<String,Object> map  = new HashMap<>();
        map.put("dateList",dateList);
        map.put("payCountList",payCountList);
        map.put("PayReturnCountList",PayReturnCountList);
        map.put("payEndCountList",payEndCountList);
        map.put("collectCountList",collectCountList);
        map.put("collectEndCountList",collectEndCountList);
        map.put("collectReturnCountList",collectReturnCountList);


        return map;
    }
}

package com.ms.merchant.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.common.core.utils.DateUtils;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.domain.MerchantPayAddress;
import com.ms.merchant.domain.MerchantPayOrder;
import com.ms.merchant.domain.vo.HomeCountVO;
import com.ms.merchant.domain.vo.OrderHeaderVo;
import com.ms.merchant.mapper.MerchantPayOrderMapper;
import com.ms.merchant.service.IMerchantPayOrderService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MerchantPayOrderServiceImpl extends ServiceImpl<MerchantPayOrderMapper, MerchantPayOrder>  implements IMerchantPayOrderService {
    @Override
    public IPage<MerchantPayOrder> selectMerchantPayOrderList(IPage<MerchantPayOrder> page, MerchantPayOrder merchantPayOrder) {

        if ("0".equals(merchantPayOrder.getMessage())){
            merchantPayOrder.setPrivateKey("0");
            merchantPayOrder.setMessage(null);
        }
        page =   baseMapper.selectOrder(page,merchantPayOrder);
        return page;
    }

    @Override
    public HomeCountVO selectHomeCount() {
        HomeCountVO homeCountVO = this.baseMapper.selectHomeCollectCount(CommonRequestHolder.getCurrentUserId());

        HomeCountVO homeCountVO1 = this.baseMapper.selectHomePayCount(CommonRequestHolder.getCurrentUserId());
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
            HomeCountVO pay =  this.baseMapper.selectHomeCountPay(date,CommonRequestHolder.getCurrentUserId());
            HomeCountVO collect =  this.baseMapper.selectHomeCountCollect(date,CommonRequestHolder.getCurrentUserId());
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

    @Override
    public List<MerchantPayAddress> selectMerchantPayOrderExport(MerchantPayOrder merchantPayOrder) {
        merchantPayOrder.setMerchantId(CommonRequestHolder.getCurrentUserId().toString());
        return   baseMapper.selectOrderExport(merchantPayOrder);

    }

    @Override
    public Map collectionHeader() {
        OrderHeaderVo todaySummer =    baseMapper.collectionTodayHeader(CommonRequestHolder.getCurrentUserId());
        OrderHeaderVo allSummer =    baseMapper.collectionHeader(CommonRequestHolder.getCurrentUserId());

        Map<String ,OrderHeaderVo> map = new HashMap<>();
        map.put("today",todaySummer);
        map.put("all",allSummer);

        return  map;

    }

    @Override
    public Map payHeader() {
        //途中
        OrderHeaderVo passageSummer =    baseMapper.passageHeader(CommonRequestHolder.getCurrentUserId());

        OrderHeaderVo successSummer =    baseMapper.successHeader(CommonRequestHolder.getCurrentUserId());

        OrderHeaderVo failSummer =    baseMapper.failHeader(CommonRequestHolder.getCurrentUserId());

        Map<String ,OrderHeaderVo> map = new HashMap<>();
        map.put("passage",passageSummer);
        map.put("successSummer",successSummer);
        map.put("failSummer",failSummer);

        return map;
    }
}

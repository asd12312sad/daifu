package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.BannerMapper;
import com.hjh.erp.domain.Banner;
import com.hjh.erp.service.IBannerService;

/**
 * 轮播图Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper,Banner> implements IBannerService
{



    /**
     * 查询轮播图列表
     *
     * @param banner 轮播图
     * @return 轮播图集合
     */
    @Override
    public List<Banner> selectBannerList(Banner banner){
        if (banner.getParams() == null){
            banner.setParams(new HashMap<>());
        }
       return  this.baseMapper.selectBannerList(banner,banner.getParams());

    }
}

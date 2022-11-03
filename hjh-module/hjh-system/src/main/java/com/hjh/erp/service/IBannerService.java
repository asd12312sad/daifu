package com.hjh.erp.service;

import com.hjh.erp.domain.Banner;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 轮播图Service接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface IBannerService  extends IService<Banner>
{

    /**
     * 查询轮播图列表
     *
     * @param banner 轮播图
     * @return 轮播图集合
     */
    public List<Banner> selectBannerList(Banner banner);

}

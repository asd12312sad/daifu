package com.hjh.erp.mapper;

import com.hjh.erp.domain.Banner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 轮播图Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface BannerMapper extends BaseMapper<Banner>
{

/**
 * 查询轮播图列表
 *
 * @param banner 轮播图
 * @return 轮播图集合
 */
public List<Banner> selectBannerList(@Param("banner")Banner banner, @Param("params") Map<String, Object> params);


    /**
     * 查询轮播图
     *
     * @param id 轮播图主键
     * @return 轮播图
     */
    public Banner selectBannerById(Long id);

}

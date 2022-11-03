package com.ms.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.domain.R;
import com.ms.student.domain.Banner;
import com.ms.student.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图Controller
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@RestController
@RequestMapping("/banner")
public class BannerController  {
    @Autowired
    private BannerMapper bannerMapper;

    /**
     * 查询轮播图列表
     */
    @GetMapping("/list")
    public R list() {
        List<Banner> page = bannerMapper.selectList(new QueryWrapper<Banner>().eq("status", 1).orderByAsc("sort"));
        return R.success(page);
    }


}

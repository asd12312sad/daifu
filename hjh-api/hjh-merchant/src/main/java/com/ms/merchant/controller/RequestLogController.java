package com.ms.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.common.domain.R;
import com.ms.common.domain.RequestLog;
import com.ms.common.utils.CommonRequestHolder;
import com.ms.merchant.mapper.RequestLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("log")
public class RequestLogController {

    @Autowired
    private RequestLogMapper requestLogMapper;

    /**
     * 查询轮播图列表
     */
    @GetMapping("/list")
    public R list(String name, Integer type,String merchantUserName, Integer status, Integer pageSize, Integer pageNumber, String startTime, String endTime) {

        QueryWrapper<RequestLog> queryWrapper = new QueryWrapper<RequestLog>();
        queryWrapper.eq("merchant_id", CommonRequestHolder.getCurrentUserId());

        if (merchantUserName != null) {
        queryWrapper.like("merchant_user_name", merchantUserName);
        }
        if (name != null && !"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if (type != null) {
            queryWrapper.eq("type", type);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (startTime != null && !"".equals(startTime)) {
            queryWrapper.ge("request_time", startTime);
        }
        if (endTime != null && !"".equals(endTime)) {
            queryWrapper.le("request_time", endTime);
        }
        queryWrapper.orderByDesc("request_time");
        IPage<RequestLog> page = new Page<>(pageNumber, pageSize);
        page = requestLogMapper.selectPage(page, queryWrapper);

        return R.success(page);
    }

}

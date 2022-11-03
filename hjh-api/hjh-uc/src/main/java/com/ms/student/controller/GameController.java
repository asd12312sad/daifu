package com.ms.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.domain.R;
import com.ms.student.domain.Game;
import com.ms.student.mapper.GameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/game")
public class GameController {

    @Autowired
    private GameMapper gameMapper;

    /**
     * 查询轮播图列表
     */
    @GetMapping("/list")
    public R list() {
        List<Game> page = gameMapper.selectList(new QueryWrapper<Game>().eq("status", 1).orderByAsc("sort"));
        for (Game game : page) {
            game.setUdunKey(null);
        }
        return R.success(page);
    }





}

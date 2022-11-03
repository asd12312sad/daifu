package com.hjh.erp.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.mapper.GameMapper;
import com.hjh.erp.domain.Game;
import com.hjh.erp.service.IGameService;

/**
 * 游戏Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-14
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper,Game> implements IGameService
{



    /**
     * 查询游戏列表
     *
     * @param game 游戏
     * @return 游戏集合
     */
    @Override
    public List<Game> selectGameList(Game game){
       return  this.baseMapper.selectGameList(game,new HashMap<>());

    }
}

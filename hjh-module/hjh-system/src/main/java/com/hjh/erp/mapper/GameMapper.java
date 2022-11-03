package com.hjh.erp.mapper;

import com.hjh.erp.domain.Game;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 游戏Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface GameMapper extends BaseMapper<Game>
{

/**
 * 查询游戏列表
 *
 * @param game 游戏
 * @return 游戏集合
 */
public List<Game> selectGameList(@Param("game")Game game, @Param("params") Map<String, Object> params);


    /**
     * 查询游戏
     *
     * @param id 游戏主键
     * @return 游戏
     */
    public Game selectGameById(Long id);

}

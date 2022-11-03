package com.hjh.erp.service;

import com.hjh.erp.domain.Game;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 游戏Service接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface IGameService  extends IService<Game>
{

    /**
     * 查询游戏列表
     *
     * @param game 游戏
     * @return 游戏集合
     */
    public List<Game> selectGameList(Game game);

}

package com.hjh.erp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjh.erp.domain.Popup;

import java.util.List;


/**
 * 弹窗Service接口
 *
 * @author xiaobing
 * @date 2022-04-22 06:14:52
 */
public interface IPopupService  extends IService<Popup>
{

    /**
     * 查询弹窗列表
     *
     * @param popup 弹窗
     * @return 弹窗集合
     */
    public List<Popup> selectPopupList(Popup popup);

}

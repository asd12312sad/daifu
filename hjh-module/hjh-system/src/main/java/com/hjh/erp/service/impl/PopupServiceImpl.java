package com.hjh.erp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.domain.Popup;
import com.hjh.erp.mapper.PopupMapper;
import com.hjh.erp.service.IPopupService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 弹窗Service业务层处理
 *
 * @author xiaobing
 * @date 2022-04-22 06:14:52
 */
@Service
public class PopupServiceImpl extends ServiceImpl<PopupMapper,Popup> implements IPopupService
{



    /**
     * 查询弹窗列表
     *
     * @param popup 弹窗
     * @return 弹窗集合
     */
    @Override
    public List<Popup> selectPopupList(Popup popup){
       return  this.baseMapper.selectPopupList(popup,new HashMap<>());

    }
}

package com.hjh.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjh.erp.domain.Popup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 弹窗Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-22 06:14:52
 */
public interface PopupMapper extends BaseMapper<Popup>
{

/**
 * 查询弹窗列表
 *
 * @param popup 弹窗
 * @return 弹窗集合
 */
public List<Popup> selectPopupList(@Param("popup")Popup popup, @Param("params") Map<String, Object> params);


    /**
     * 查询弹窗
     *
     * @param id 弹窗主键
     * @return 弹窗
     */
    public Popup selectPopupById(Long id);

}

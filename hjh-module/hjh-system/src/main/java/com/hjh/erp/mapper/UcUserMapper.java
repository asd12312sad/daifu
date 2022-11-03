package com.hjh.erp.mapper;

import com.hjh.erp.domain.UcUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-14
 */
public interface UcUserMapper extends BaseMapper<UcUser>
{

/**
 * 查询会员列表
 *
 * @param ucUser 会员
 * @return 会员集合
 */
public List<UcUser> selectUcUserList(@Param("ucUser")UcUser ucUser, @Param("params") Map<String, Object> params);


    /**
     * 查询会员
     *
     * @param id 会员主键
     * @return 会员
     */
    public UcUser selectUcUserById(Long id);

}

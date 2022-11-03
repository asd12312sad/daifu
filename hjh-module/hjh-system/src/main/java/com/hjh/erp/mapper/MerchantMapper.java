package com.hjh.erp.mapper;

import com.hjh.erp.domain.Merchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商户管理Mapper接口
 *
 * @author xiaobing
 * @date 2022-06-02
 */
public interface MerchantMapper extends BaseMapper<Merchant>
{

/**
 * 查询商户管理列表
 *
 * @param merchant 商户管理
 * @return 商户管理集合
 */
public List<Merchant> selectMerchantList(@Param("merchant")Merchant merchant, @Param("params") Map<String, Object> params);


    /**
     * 查询商户管理
     *
     * @param id 商户管理主键
     * @return 商户管理
     */
    public Merchant selectMerchantById(Long id);

}

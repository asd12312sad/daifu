package com.hjh.erp.mapper;

import com.hjh.erp.domain.WithdrawAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 提币审核Mapper接口
 *
 * @author xiaobing
 * @date 2022-04-21
 */
public interface WithdrawAuditMapper extends BaseMapper<WithdrawAudit>
{

/**
 * 查询提币审核列表
 *
 * @param withdrawAudit 提币审核
 * @return 提币审核集合
 */
public List<WithdrawAudit> selectWithdrawAuditList(@Param("withdrawAudit")WithdrawAudit withdrawAudit, @Param("params") Map<String, Object> params);


    /**
     * 查询提币审核
     *
     * @param id 提币审核主键
     * @return 提币审核
     */
    public WithdrawAudit selectWithdrawAuditById(Long id);

}

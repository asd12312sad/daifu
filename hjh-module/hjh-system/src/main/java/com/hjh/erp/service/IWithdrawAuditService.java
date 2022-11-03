package com.hjh.erp.service;

import com.hjh.erp.domain.WithdrawAudit;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


/**
 * 提币审核Service接口
 *
 * @author xiaobing
 * @date 2022-04-21
 */
public interface IWithdrawAuditService  extends IService<WithdrawAudit>
{

    /**
     * 查询提币审核列表
     *
     * @param withdrawAudit 提币审核
     * @return 提币审核集合
     */
    public List<WithdrawAudit> selectWithdrawAuditList(WithdrawAudit withdrawAudit);

    boolean audit(WithdrawAudit withdrawAudit);
}

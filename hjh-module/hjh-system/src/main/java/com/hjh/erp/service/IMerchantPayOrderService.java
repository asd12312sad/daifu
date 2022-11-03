package com.hjh.erp.service;

import com.hjh.erp.domain.HomeCountVO;
import com.hjh.erp.domain.MerchantPayOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;


/**
 * 商户代付记录Service接口
 *
 * @author xiaobing
 * @date 2022-06-10
 */
public interface IMerchantPayOrderService  extends IService<MerchantPayOrder>
{

    /**
     * 查询商户代付记录列表
     *
     * @param merchantPayOrder 商户代付记录
     * @return 商户代付记录集合
     */
    public List<MerchantPayOrder> selectMerchantPayOrderList(MerchantPayOrder merchantPayOrder);

    HomeCountVO selectHomeCount();

    Map<String,Object> weekSum();

}

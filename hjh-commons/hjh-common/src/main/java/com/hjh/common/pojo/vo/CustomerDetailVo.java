package com.hjh.common.pojo.vo;

import com.hjh.common.domain.CmsCustomerFile;
import com.hjh.common.domain.CmsCustomerVisit;
import com.hjh.common.domain.Customer;
import com.hjh.common.domain.CustomerAccess;
import lombok.Data;

import java.util.List;

/**
 * 客户明细
 *
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/8/24 15:03
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/8/24 15:03        肖兵           v1.0.0           Created
 */
@Data
public class CustomerDetailVo {

    private Customer customer;
    /**
     * 文件列表
     */
    private List<CmsCustomerFile> fileList;

    /**
     * 跟访记录
     */
    private List<CmsCustomerVisit> visitList;

    /**
     * 客户联系人
     */
    private List<CustomerAccess> customerAccessList;
}

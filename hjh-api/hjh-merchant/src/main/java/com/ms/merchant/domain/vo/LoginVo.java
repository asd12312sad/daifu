package com.ms.merchant.domain.vo;

import com.ms.merchant.domain.MerchantMenu;
import lombok.Data;

import java.util.List;

/**
 * LoginVo -- 登录返回Vo
 *
 * @author XiaoBing
 * @version v1.0.0
 * @date 2020/12/8
 * @Description Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2020/12/8             XiaoBing          v1.0.0           Created
 */
@Data
public class LoginVo {


    private String tokenType;

    private String token;

    private List<MerchantMenu> menuList;


}

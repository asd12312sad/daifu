package com.hjh.erp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.erp.domain.MerchantUser;
import com.hjh.erp.mapper.MerchantUserMapper;
import com.hjh.erp.service.MerchantUserService;
import org.springframework.stereotype.Service;

@Service
public class MerchantUserServiceImpl  extends ServiceImpl<MerchantUserMapper, MerchantUser> implements MerchantUserService {
}

package com.ms.agent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ms.agent.domain.MerchantUser;
import com.ms.agent.mapper.MerchantUserMapper;
import com.ms.agent.service.MerchantUserService;
import org.springframework.stereotype.Service;

@Service
public class MerchantUserServiceImpl extends ServiceImpl<MerchantUserMapper, MerchantUser> implements MerchantUserService {
}

package com.ms.common.config;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS对象存储相关配置
 * Created by macro on 2018/5/17.
 */
@Configuration
public class OssConfig {
    private final String ALIYUN_OSS_ENDPOINT = "oss-cn-beijing.aliyuncs.com";
    private final String ALIYUN_OSS_ACCESSKEYID = "LTAI5tGc87Z2sYLHDi4evw4u";
    private final String ALIYUN_OSS_ACCESSKEYSECRET = "MadkBkRllfAxXHsNAr1rn9p3fkPis0";
    @Bean
    public OSSClient ossClient(){
        return new OSSClient(ALIYUN_OSS_ENDPOINT,ALIYUN_OSS_ACCESSKEYID,ALIYUN_OSS_ACCESSKEYSECRET);
    }
}

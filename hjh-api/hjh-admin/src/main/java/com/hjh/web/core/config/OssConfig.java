package com.hjh.web.core.config;

import com.aliyun.oss.OSSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS对象存储相关配置
 * Created by macro on 2018/5/17.
 */
@Configuration
public class OssConfig {
    private final String ALIYUN_OSS_ENDPOINT = "oss-cn-beijing.aliyuncs.com";
    private final String ALIYUN_OSS_ACCESSKEYID = "*";
    private final String ALIYUN_OSS_ACCESSKEYSECRET = "*";

    @Bean
    public OSSClient ossClient() {
        return new OSSClient(ALIYUN_OSS_ENDPOINT, ALIYUN_OSS_ACCESSKEYID, ALIYUN_OSS_ACCESSKEYSECRET);
    }
}

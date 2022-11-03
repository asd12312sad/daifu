package com.hjh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author hjh
 */
@MapperScan(basePackages = {"com.hjh.*.mapper", "com.xiaohe.*.mapper", "com.baomidou.mybatisplus.samples.quickstart.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
@SpringBootApplication(scanBasePackages = {"com.hjh"}, exclude = {DataSourceAutoConfiguration.class})

public class HeJiaHeApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(HeJiaHeApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  合家合启动成功   ლ(´ڡ`ლ)ﾞ  \n");
    }
}

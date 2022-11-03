package com.ms.merchant;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * 用户模块
 *
 * @author xiaobing
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.ms"})
@MapperScan(basePackages = { "com.ms.*.mapper" ,"com.baomidou.mybatisplus.samples.quickstart.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class  MerchantApplication {
    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }
}

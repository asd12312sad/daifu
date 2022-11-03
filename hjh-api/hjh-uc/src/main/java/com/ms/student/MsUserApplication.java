package com.ms.student;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户模块
 *
 * @author xiaobing
 */
@Slf4j
@EnableScheduling
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = {"com.ms"})
@MapperScan(basePackages = { "com.ms.*.mapper" ,"com.baomidou.mybatisplus.samples.quickstart.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MsUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsUserApplication.class, args);
    }
}

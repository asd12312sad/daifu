package com.ms.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ms.common.aop.MyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器注册
 *
 * @author xiaobing
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RedisTemplate<String, String> redisTemplate;

    private final IgnoreUrlsConfig ignoreUrlsConfig;

    public WebConfig(RedisTemplate<String, String> redisTemplate, IgnoreUrlsConfig ignoreUrlsConfig) {
        this.redisTemplate = redisTemplate;
        this.ignoreUrlsConfig = ignoreUrlsConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("*")
                .allowedOrigins("*");
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 跨域支持
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") //浏览器允许所有的域访问 / 注意 * 不能满足带有cookie的访问,Origin 必须是全匹配
                        .allowedMethods("*")
                        .allowCredentials(true)  // 允许带cookie访问
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
        };
    }
    /**
     * 配置分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //SpringMVC下，拦截器的注册需要排除对静态资源的拦截(*.css,*.js)
        //SpringBoot已经做好了静态资源的映射，因此我们无需任何操作
        registry.addInterceptor(new MyInterceptor(redisTemplate, ignoreUrlsConfig)).addPathPatterns("/**")
                .excludePathPatterns("/index.html", "/", "/user/login");
    }


}

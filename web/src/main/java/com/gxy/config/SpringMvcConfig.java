package com.gxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Classname SpringMvcConfig
 * @Date 2024/7/25
 * @Created by guoxinyu
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
//                //过滤不需要拦截的路径
//                .excludePathPatterns(
//                        "/public/login",
//                        "/captcha/**",
//                        "/full-case-analysis/public/**"
//                );
//
//    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://www.zhipin.com","http://10.253.50.31:8091/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowCredentials(true);
    }
}

//TODO配置拦截器、跨域、静态资源


package com.pwenjie.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(    // 排除公开接口
                        // 测试接口
                        "/api/test/**",

                        // 用户公开接口
                        "/api/users/login",
                        "/api/users/register",

                        // 文档相关
                        "/api/doc.html",
                        "/api/swagger**/**",
                        "/api/webjars/**",
                        "/api/v2/api-docs",
                        "/api/v3/api-docs",

                        // 静态资源
                        "/api/favicon.ico",
                        "/api/error",

                        // 前端页面
                        "/",
                        "/index.html",
                        "/css/**",
                        "/js/**"
                )
                .order(1);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 所有接口
                .allowedOriginPatterns("*")  // 允许所有域
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

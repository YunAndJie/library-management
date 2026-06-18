package com.pwenjie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("在线图书管理系统 API")
                        .version("1.0.0")
                        .description("图书管理系统后端接口文档")
                        .contact(new Contact()
                                .name("PWJ")
                                .email("admin@example.com")));
    }
}

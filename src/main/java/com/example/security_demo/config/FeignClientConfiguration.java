package com.example.security_demo.config;

import com.example.security_demo.service.storageService.FeignClientInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor(FeignClientInterceptor feignClientInterceptor){
        return feignClientInterceptor;
    }
}

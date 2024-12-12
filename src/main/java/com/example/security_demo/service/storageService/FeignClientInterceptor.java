package com.example.security_demo.service.storageService;

import com.example.security_demo.service.UserKeycloakService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class FeignClientInterceptor implements RequestInterceptor {
    private UserKeycloakService userKeycloakService;

    public FeignClientInterceptor(UserKeycloakService userKeycloakService) {
        this.userKeycloakService = userKeycloakService;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = "Bearer " + userKeycloakService.token().getAccessToken();
        log.info("Adding Bearer token to request: {}", requestTemplate.url());
        requestTemplate.header("Authorization", token);
    }
}

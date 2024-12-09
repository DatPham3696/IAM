package com.example.security_demo.securityConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.security.authentication.jwt")
public class AuthenticationProperties {
    private String keyStore;
    private String keyStorePassword;
    private String keyAlias;
}

package com.example.security_demo.config;

import com.example.security_demo.securityConfig.AuthenticationProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

@Component
@EnableConfigurationProperties(AuthenticationProperties.class)
@Data
@Slf4j
public class TokenProvider implements InitializingBean {
    private final AuthenticationProperties properties;
    private KeyPair keyPair;
    public TokenProvider(AuthenticationProperties properties){
        this.properties = properties;
    }
    private KeyPair keyPair(String keyStore, String password, String alias){
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keyStore), password.toCharArray());
        return keyStoreKeyFactory.getKeyPair(alias);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.keyPair = keyPair(properties.getKeyStore(),
                properties.getKeyStorePassword(),
                properties.getKeyAlias());
    }
}

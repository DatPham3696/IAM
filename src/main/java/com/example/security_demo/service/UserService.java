package com.example.security_demo.service;

import com.example.security_demo.dtos.userDtos.LogoutRequest;
import com.example.security_demo.dtos.userDtos.RefreshTokenRequest;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.dtos.userDtos.UserResponseDTO;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.service.keyCloakService.IUserServiceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserServiceStrategy {
    @Autowired
    private UserKeycloakService userKeycloakService;
    @Autowired DefaultUserService defaultUserService;
    @Value("${idp.enabled}")
    private boolean keycloakEnabled;
    @Override
    public ResponseEntity<?> register(RegisterDTO registerDTO) throws UserExistedException {
        if(keycloakEnabled){
            return ResponseEntity.ok().body(userKeycloakService.createKeycloakUser(registerDTO));
        }else {
            return ResponseEntity.ok().body(defaultUserService.register(registerDTO));
        }
    }
    @Override
    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        if(keycloakEnabled){
            return ResponseEntity.ok().body(userKeycloakService.refreshToken(request));
        }else {
            return ResponseEntity.ok().body(defaultUserService.refreshToken(request));
        }
    }

    @Override
    public ResponseEntity<?> logout(String accessToken, String refreshToken) {
        if(keycloakEnabled){
            return ResponseEntity.ok().body(userKeycloakService.logout(accessToken, refreshToken));
        }else {
            return ResponseEntity.ok().body(defaultUserService.logout(accessToken, refreshToken));
        }
    }

}


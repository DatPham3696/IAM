package com.example.security_demo.service;

import com.example.security_demo.dto.request.user.EnableUserRequest;
import com.example.security_demo.dto.request.user.RefreshTokenRequest;
import com.example.security_demo.dto.request.user.RegisterDTO;
import com.example.security_demo.dto.request.user.ResetPasswordRequest;
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
    @Autowired
    DefaultUserService defaultUserService;
    @Value("${idp.enabled}")
    private boolean keycloakEnabled;

    @Override
    public ResponseEntity<?> register(RegisterDTO registerDTO) throws UserExistedException {
        if (keycloakEnabled) {
            return ResponseEntity.ok().body(userKeycloakService.createKeycloakUser(registerDTO));
        } else {
            return ResponseEntity.ok().body(defaultUserService.register(registerDTO));
        }
    }

    @Override
    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        if (keycloakEnabled) {
            return ResponseEntity.ok().body(userKeycloakService.refreshToken(request));
        } else {
            return ResponseEntity.ok().body(defaultUserService.refreshToken(request));
        }
    }

    @Override
    public ResponseEntity<?> logout(String accessToken, String refreshToken) {
        if (keycloakEnabled) {
            return ResponseEntity.ok().body(userKeycloakService.logout(accessToken, refreshToken));
        } else {
            return ResponseEntity.ok().body(defaultUserService.logout(accessToken, refreshToken));
        }
    }

    @Override
    public ResponseEntity<?> enableUser(String accessToken, String userId, EnableUserRequest request) {
        if (keycloakEnabled) {
            return ResponseEntity.ok().body(userKeycloakService.enableUser(accessToken, userId, request));
        } else {
            return ResponseEntity.ok().body(defaultUserService.enableUser(userId, request));
        }
    }

    @Override
    public ResponseEntity<?> resetPassword(String accessToken, String userId, ResetPasswordRequest request) {
        userKeycloakService.resetPassword(accessToken, userId, request);
        defaultUserService.resetPassword(userId, request);
        return ResponseEntity.ok().body("Change password successful");
    }

    @Override
    public ResponseEntity<?> getUserInfor(String accessToken) {
        if (keycloakEnabled) {
            return ResponseEntity.ok().body(userKeycloakService.getUserInfor(accessToken));
        }
//        }else{
//            return ResponseEntity.ok().body(defaultUserService.getUserInfor(accessToken));
//        }
        return null;
    }


}


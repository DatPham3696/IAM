package com.example.security_demo.service.keyCloakService;

import com.example.security_demo.dtos.userDtos.LogoutRequest;
import com.example.security_demo.dtos.userDtos.RefreshTokenRequest;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.dtos.userDtos.UserResponseDTO;
import com.example.security_demo.exception.UserExistedException;
import org.springframework.http.ResponseEntity;

public interface IUserServiceStrategy {
    ResponseEntity<?> register(RegisterDTO registerDTO)  throws UserExistedException;
    ResponseEntity<?> refreshToken(RefreshTokenRequest request);
    ResponseEntity<?> logout(String accessToken, String refreshToken);
}

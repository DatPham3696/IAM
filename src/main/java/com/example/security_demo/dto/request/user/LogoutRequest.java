package com.example.security_demo.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    private String refreshToken;
    private String token;
}

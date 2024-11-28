package com.example.security_demo.dtos.userDtos;

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

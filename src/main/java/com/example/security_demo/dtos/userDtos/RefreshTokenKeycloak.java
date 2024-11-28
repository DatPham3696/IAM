package com.example.security_demo.dtos.userDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenKeycloak {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String refresh_token;
}

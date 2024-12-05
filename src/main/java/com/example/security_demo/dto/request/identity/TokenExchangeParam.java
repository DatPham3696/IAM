package com.example.security_demo.dto.request.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenExchangeParam {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String scope;
}

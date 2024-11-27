package com.example.security_demo.repository;

import com.example.security_demo.dtos.identity.TokenExchangeParam;
import com.example.security_demo.dtos.identity.TokenExchangeResponse;
import com.example.security_demo.dtos.identity.UserCreationParam;
import com.example.security_demo.entity.Logout;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url="${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/IAM/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam tokenExchangeParam);
    @PostMapping(value = "/admin/realms/IAM/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);
    @PostMapping(value = "/realms/IAM/protocol/openid-connect/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("authorization") String authorizationHeader, @QueryMap Logout logout);
}

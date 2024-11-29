package com.example.security_demo.service.keyCloakService;

import com.example.security_demo.dtos.identity.TokenExchangeParam;
import com.example.security_demo.dtos.identity.TokenExchangeResponse;
import com.example.security_demo.dtos.identity.UserCreationParam;
import com.example.security_demo.dtos.userDtos.*;
import com.example.security_demo.entity.Logout;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/IAM/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam tokenExchangeParam);

    @PostMapping(value = "/admin/realms/IAM/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);

    @PostMapping(value = "/realms/IAM/protocol/openid-connect/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("authorization") String authorizationHeader, @QueryMap Logout logout);

    @PostMapping(value = "/realms/IAM/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> refeshToken(@QueryMap RefreshTokenKeycloak request);

    @PutMapping(value = "/admin/realms/IAM/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> enableUser(@RequestHeader("authorization") String authorizationHeader,
                                        @PathVariable("userId") String userId, @RequestBody EnableUserRequest request);

    @PutMapping(value = "/admin/realms/IAM/users/{userId}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestHeader("authorization") String authorizationHeader,
                                           @PathVariable("userId") String userId, @RequestBody ResetPasswordKclRequest request);
    @GetMapping(value = "/realms/IAM/protocol/openid-connect/userinfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> getUserInfor(@RequestHeader("authorization") String authorizationHeader);
}

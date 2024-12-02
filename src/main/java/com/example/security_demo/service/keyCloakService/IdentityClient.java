package com.example.security_demo.service.keyCloakService;

import com.example.security_demo.dto.request.user.EnableUserRequest;
import com.example.security_demo.dto.request.user.RefreshTokenKeycloak;
import com.example.security_demo.dto.request.user.ResetPasswordKclRequest;
import com.example.security_demo.dto.request.identity.TokenExchangeParam;
import com.example.security_demo.dto.request.identity.TokenExchangeResponse;
import com.example.security_demo.dto.request.identity.UserCreationParam;
import com.example.security_demo.entity.Logout;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "${idp.endpoints.token}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam tokenExchangeParam);

    @PostMapping(value = "${idp.endpoints.create-user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);

    @PostMapping(value = "${idp.endpoints.logout}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("authorization") String authorizationHeader, @QueryMap Logout logout);

    @PostMapping(value = "${idp.endpoints.token}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> refeshToken(@QueryMap RefreshTokenKeycloak request);

    @PutMapping(value = "${idp.endpoints.enable-user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> enableUser(@RequestHeader("authorization") String authorizationHeader,
                                        @PathVariable("userId") String userId, @RequestBody EnableUserRequest request);

    @PutMapping(value = "${idp.endpoints.reset-password}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestHeader("authorization") String authorizationHeader,
                                           @PathVariable("userId") String userId, @RequestBody ResetPasswordKclRequest request);

    @GetMapping(value = "${idp.endpoints.user-info}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> getUserInfor(@RequestHeader("authorization") String authorizationHeader);
}

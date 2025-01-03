package com.example.security_demo.service;

import com.example.security_demo.dto.request.user.*;
import com.example.security_demo.dto.request.identity.Credential;
import com.example.security_demo.dto.request.identity.TokenExchangeParam;
import com.example.security_demo.dto.request.identity.TokenExchangeResponse;
import com.example.security_demo.dto.request.identity.UserCreationParam;
import com.example.security_demo.entity.Logout;
import com.example.security_demo.service.keyCloakService.IdentityClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKeycloakService {
    @Value("${idp.client-id}")
    String clientId;
    @Value(("${idp.client-secret}"))
    String clientSecret;
    @Value("${idp.enabled}")
    private boolean keycloakEnabled;
    private final IdentityClient identityClient;

    public ResponseEntity<?> createKeycloakUser(RegisterDTO registerDTO) {
        return identityClient.createUser("Bearer " + token().getAccessToken(), UserCreationParam.builder()
                .username(registerDTO.getUserName())
                .firstName(registerDTO.getUserName())
                .lastName(registerDTO.getUserName())
                .email(registerDTO.getEmail())
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(Credential.builder()
                        .type("password")
                        .temporary(false)
                        .value(registerDTO.getPassWord())
                        .build()))
                .build());
    }

    public String getKeycloakUserId(RegisterDTO registerDTO) {
        return extractUserId(createKeycloakUser(registerDTO));
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").getFirst();
        String[] paths = location.split("/");
        return paths[paths.length - 1];
    }

    public TokenExchangeResponse token() {
        return identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());
    }

    public String logout(String authorizationHeader, String refreshToken) {
        ResponseEntity<?> response = identityClient.logout(authorizationHeader, Logout.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .refresh_token(refreshToken)
                .build());
        if (response.getStatusCode().is2xxSuccessful()) {
            return "User logged out successfully from Keycloak.";
        } else {
            return "Error logging out from Keycloak: " + response.getStatusCode();
        }
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        return identityClient.refreshToken(RefreshTokenKeycloak.builder()
                .grant_type("refresh_token")
                .client_id(clientId)
                .client_secret(clientSecret)
                .refresh_token(request.getRefreshToken())
                .build());
    }

    public ResponseEntity<?> enableUser(String authorizationHeader, String userId, EnableUserRequest request) {
        return identityClient.enableUser(authorizationHeader, userId, request);
    }

    public ResponseEntity<?> resetPassword(String authorizationHeader, String userId, ResetPasswordRequest request) {
        String token = keycloakEnabled ? authorizationHeader : "Bearer " + token().getAccessToken();

        ResetPasswordKclRequest resetPasswordRequest = ResetPasswordKclRequest.builder()
                .type("password")
                .value(request.getNewPassword())
                .temporary(false)
                .build();

        return identityClient.resetPassword(token, userId, resetPasswordRequest);
    }

    public ResponseEntity<?> getUserInfor(String authorizationHeader) {
        return identityClient.getUserInfor(authorizationHeader);
    }
}

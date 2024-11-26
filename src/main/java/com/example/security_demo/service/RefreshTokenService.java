package com.example.security_demo.service;

import com.example.security_demo.config.JwtTokenUtils;
import com.example.security_demo.entity.RefreshToken;
import com.example.security_demo.repository.IRefreshTokenRepository;
import com.example.security_demo.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${spring.security.authentication.jwt.jwt_refresh_expiration}")
    private Long refreshTokenDuration ;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final IUserRepository userRepository;
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
    public RefreshToken createRefreshToken(Long userId, String accessTokenId){
        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setId(1L); //
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setAccessTokenId(accessTokenId);

        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken verifyRefreshToken(RefreshToken token){
        if(token.getExpiryDate().before(new Date())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Token was expired");
        }
        return token;
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}

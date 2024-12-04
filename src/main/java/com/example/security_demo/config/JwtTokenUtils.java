package com.example.security_demo.config;

import com.example.security_demo.entity.*;
import com.example.security_demo.repository.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtTokenUtils {
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final IInvalidTokenRepository invalidTokenRepository;
    private final IRoleUserRepository roleUserRepository;
    @Autowired
    private TokenProvider tokenProvider;
    @Value("${spring.security.authentication.jwt.jwt_refresh_expiration}")
    private Long refreshTokenDuration;

    public JwtTokenUtils(IRoleRepository roleRepository, IPermissionRepository permissionRepository,
                         IRolePermissionRepository rolePermissionRepository, IInvalidTokenRepository invalidTokenRepository,
                         IRoleUserRepository roleUserRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.invalidTokenRepository = invalidTokenRepository;
        this.roleUserRepository = roleUserRepository;
    }

    public String generateToken(User user) {
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        String roleName = roleRepository.findById(roleUser.getRoleId()).get().getCode();
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + 300000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("sub", user.getEmail());
        claims.put("exp", expirationDate);
        claims.put("scope", roleName);
        claims.put("jti", UUID.randomUUID().toString());
        String JWT = Jwts.builder().claims(claims)
                .signWith(tokenProvider.getKeyPair().getPrivate(), Jwts.SIG.RS256)
                .compact();
        return JWT;
    }
    public String generaRefreshToken(User user) {
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        String roleName = roleRepository.findById(roleUser.getRoleId()).get().getCode();
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + refreshTokenDuration);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("sub", user.getEmail());
        claims.put("exp", expirationDate);
        claims.put("scope", roleName);
        claims.put("jti", UUID.randomUUID().toString());
        String JWT = Jwts.builder().claims(claims)
                .signWith(tokenProvider.getKeyPair().getPrivate(), Jwts.SIG.RS256)
                .compact();
        return JWT;
    }


    public Claims getAllClaimFromToken(String token) {
        return Jwts.parser()
                .verifyWith(tokenProvider.getKeyPair().getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getSubFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationTimeFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getJtiFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationTimeFromToken(token);
        return expirationDate.before(new Date());
    }

    public boolean isTokenValid(String token) {
        String jti = getJtiFromToken(token);
        if(invalidTokenRepository.existsById(jti)){
            if(isTokenExpired(token)){
                invalidTokenRepository.deleteById(jti);
            }
            return true;
        }
        return false;
    }
}

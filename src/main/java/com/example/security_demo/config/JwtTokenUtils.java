package com.example.security_demo.config;

import com.example.security_demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtils {
//    @Value("${jwt.secret}")
    private final String secretKey = "yPTyD4NJtTyXxf9v+Y9bPerZs6XtiCyD+fNdlB/lRmdq4UrpOK6brnicDMZXbgiq";
    public String generateToken( User user){
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + 86400000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",user.getUsername());
        claims.put("sub",user.getUsername());
        claims.put("exp", expirationDate);
        claims.put("scope", user.getRoles());
        String JWT = Jwts.builder().claims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return JWT;
    }
    private SecretKey getSignKey(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
    }
    public Claims getAllClaimFromToken(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String exTractUserName(String token){
        return getAllClaimFromToken(token).getSubject();
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){
        Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }
    public String getUserNameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationTimeFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        Date expirationDate = getExpirationTimeFromToken(token);
        return expirationDate.before(new Date());
    }
}

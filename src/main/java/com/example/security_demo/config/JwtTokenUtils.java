package com.example.security_demo.config;

import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RolePermission;
import com.example.security_demo.entity.User;
import com.example.security_demo.repository.IPermissionRepository;
import com.example.security_demo.repository.IRolePermissionRepository;
import com.example.security_demo.repository.IRoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
    //    @Value("${jwt.secret}")
    //private final String secretKey = "yPTyD4NJtTyXxf9v+Y9bPerZs6XtiCyD+fNdlB/lRmdq4UrpOK6brnicDMZXbgiq";
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    @Autowired
    private TokenProvider tokenProvider;

    public JwtTokenUtils(IRoleRepository roleRepository, IPermissionRepository permissionRepository, IRolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    //    public String generateToken(User user){
//        String role = roleRepository.findById(user.getRoleId()).get().getRoleName();
//        List<Long> permissionId = rolePermissionRepository.findAllByRoleId(user.getRoleId()).stream()
//                .map(RolePermission::getPermissionId).collect(Collectors.toList());
//        List<String> description = permissionRepository.findAllById(permissionId).stream().map(Permission::getDescription).toList();
//        String scope = role + " " + String.join(" ", description);
//        long currentTimeMillis = System.currentTimeMillis();
//        Date expirationDate = new Date(currentTimeMillis + 86400000);
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("username",user.getUsername());
//        claims.put("sub",user.getEmail());
//        claims.put("exp", expirationDate);
//        claims.put("scope", scope);
//        String JWT = Jwts.builder().claims(claims)
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//        return JWT;
//    }
    public String generateToken(User user) {
        String role = roleRepository.findById(user.getRoleId()).get().getRoleName();
        List<Long> permissionId = rolePermissionRepository.findAllByRoleId(user.getRoleId()).stream()
                .map(RolePermission::getPermissionId).collect(Collectors.toList());
        List<String> description = permissionRepository.findAllById(permissionId).stream().map(Permission::getDescription).toList();
        String scope = role + " " + String.join(" ", description);
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + 86400000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("sub", user.getEmail());
        claims.put("exp", expirationDate);
        claims.put("scope", scope);
        String JWT = Jwts.builder().claims(claims)
                .signWith(tokenProvider.getKeyPair().getPrivate(), Jwts.SIG.RS256)
                .compact();
        return JWT;
    }

//    private SecretKey getSignKey() {
//        byte[] bytes = Base64.getDecoder().decode(secretKey);
//        return new SecretKeySpec(bytes, tokenProvider.getKeyPair().getPublic());
//    }

    public Claims getAllClaimFromToken(String token) {
        return Jwts.parser()
                .verifyWith(tokenProvider.getKeyPair().getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String exTractUserName(String token) {
        return getAllClaimFromToken(token).getSubject();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationTimeFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationTimeFromToken(token);
        return expirationDate.before(new Date());
    }
}

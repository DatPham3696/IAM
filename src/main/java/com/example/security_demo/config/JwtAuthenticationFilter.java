package com.example.security_demo.config;

//import com.example.security_demo.service.UserInforDetailService;
import com.example.security_demo.service.UserInforDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@ConditionalOnProperty(name = "idp.enabled", havingValue = "false")
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserInforDetailService userDetailsService;
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "api/users/login",
            "api/users/register",
            "api/users/confirm-register-email",
            "api/users/confirm-login-email",
            "api/users/uploads",
            "api/users/refresh-token",
            "api/users/logout",
            "swagger-ui",
            "v3/api-docs",
//            "/swagger-ui/",
            "swagger-ui/index.html"
    );
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("{}" ,request.getHeader("Authorization"));
        String token = getJwtFromRequest(request);
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if(jwtTokenUtils.isTokenValid(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalid");
            return;
        }
        if(token != null && !jwtTokenUtils.isTokenExpired(token)){
            String email = jwtTokenUtils.getSubFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }
    private String getJwtFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")){
           return token.substring(7);
        }
        return null;
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return PUBLIC_ENDPOINTS.stream().anyMatch(uri::contains);
    }
}

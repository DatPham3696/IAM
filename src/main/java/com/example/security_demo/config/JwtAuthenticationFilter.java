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
@ConditionalOnProperty(name = "idp.enabled", havingValue = "false")
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserInforDetailService userDetailsService;
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
//    private boolean isKeycloakToken(String token){
//        String issuer = jwtTokenUtils.get
//    }
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.contains("api/users/login") ||
                uri.contains("api/users/register") ||
//                uri.contains("/logoutAccount")  ||
                uri.contains("api/users/confirm-register-email") ||
                uri.contains("api/users/confirm-login-email") ||
                uri.contains("api/users/uploads") ||
                uri.contains("api/users/refresh-token") ||
                uri.contains("api/users/logout") ;
    }
}

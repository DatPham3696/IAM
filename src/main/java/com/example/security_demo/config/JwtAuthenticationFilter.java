package com.example.security_demo.config;

//import com.example.security_demo.service.UserInforDetailService;
import com.example.security_demo.service.UserInforDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserInforDetailService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if(jwtTokenUtils.isTokenValid(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token in valid");
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
        return uri.contains("/login") ||
                uri.contains("/register") ||
//                uri.contains("/logoutAccount")  ||
                uri.contains("/confirmRegisterEmail") ||
                uri.contains("/confirmLoginEmail") ||
                uri.contains("/uploads");
    }
}

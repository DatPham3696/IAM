package com.example.security_demo.Config;

import com.example.security_demo.Repository.IUserRepository;
import com.example.security_demo.Service.UserInforDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserInforDetailService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if(token != null && !jwtTokenUtils.isTokenExpired(token)){
            String userName = jwtTokenUtils.getUserNameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            //lấy ra thông tin người dùng hiện tại trong ứng dụng:
            //Authentication authentication = SecurityContextHolder.getContext.getAuthentication()
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
}

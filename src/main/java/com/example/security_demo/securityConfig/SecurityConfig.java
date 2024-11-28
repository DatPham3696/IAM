package com.example.security_demo.securityConfig;

import com.example.security_demo.config.JwtAuthenticationFilter;
//import com.example.security_demo.service.UserInforDetailService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired(required = false)
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    String[] PUBLIC_ENDPOINT = {"api/login/**", "api/register/**", "api/resetPasswordToken/**",
                                "api/logoutAccount/**",
            "api/confirmRegisterEmail/**", "api/confirmLoginEmail", "api/uploads/**", "api/refreshToken/**", "api/logout/**"};
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    @Value("${idp.enabled}")
    private boolean keycloakEnabled;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        if(keycloakEnabled){
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
//                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests((request) -> request
                            .requestMatchers(PUBLIC_ENDPOINT)
                            .permitAll()
                            .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                    .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//                    .httpBasic(withDefaults());
        }else{
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests((request) -> request
                            .requestMatchers(PUBLIC_ENDPOINT)
                            .permitAll()
                            .anyRequest().authenticated())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .httpBasic(withDefaults());
        }
        return httpSecurity.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:8080/realms/IAM");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
//    @Bean
//    public UserDetailsService userDetailsService(){
//        return new UserInforDetailService();
//    }
}

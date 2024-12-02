package com.example.security_demo.securityConfig;

import com.example.security_demo.config.CustomEvaluator;
import com.example.security_demo.config.JwtAuthenticationFilter;
//import com.example.security_demo.service.UserInforDetailService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
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
    String[] PUBLIC_ENDPOINT = {"api/users/login/**", "api/users/register/**", "api/users/resetPasswordToken/**",
                                "api/users/logoutAccount/**","api/roles/create-role/**","api/permission/create-permission/**",
            "api/users/confirmRegisterEmail/**", "api/users/confirmLoginEmail", "api/users/uploads/**", "api/users/refreshToken/**", "api/users/logout/**"};
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final String[] SWAGGER_ENDPOINT = {"/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**","/swagger-ui/index.html"};
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
                            .requestMatchers(SWAGGER_ENDPOINT)
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
    @Bean
    public CustomEvaluator customEvaluator(){
        return new CustomEvaluator();
    }
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customEvaluator());
        return expressionHandler;
    }
}

package com.example.security_demo.Logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

@Component
@Order(Ordered.LOWEST_PRECEDENCE) // Ordered.LOWEST_PRECEDENCE sẽ được đặt ở cuối danh sách.
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        filterChain.doFilter(cachingRequestWrapper, servletResponse);
        if (httpServletRequest.getParameterMap().containsKey("password")) {
            logger.info("Request contains sentitive data, hiding 'password'.");
        }
        String requestBody = new String(cachingRequestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (requestBody.toLowerCase().contains("password")) {
            requestBody = requestBody.replaceAll("(?i)\"passWord\"\\s*:\\s*\"[^\"]*\"", "\"passWord\":\"******\"");
        }
        logger.info("Request - Method : {} URI: {}, Params: {}, Body: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getParameterMap()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.toString(e.getValue()))),
                requestBody
        );
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        logger.info("Response - status: {}", httpServletResponse.getStatus());
    }

}

package com.example.security_demo.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.Optional;

@Service
public class CredentialService {
    public String getCredentialInfor(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().isBlank()){
            return "Anonymous user";
        }
        return authentication.getName();
    }
}

package com.example.security_demo.controller;

import com.example.security_demo.dtos.userDtos.LoginRequestDTO;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.entity.User;
import com.example.security_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO user) {
        try{
           return ResponseEntity.ok(userService.signUp(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO userDTO) throws Exception {
        try{
            return userService.login(userDTO);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin(){
        return "admin";
    }
}

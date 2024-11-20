package com.example.security_demo.Controller;

import com.example.security_demo.DTO.UserDTO;
import com.example.security_demo.Entity.Users;
import com.example.security_demo.Exception.UserExistedException;
import com.example.security_demo.Service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        try{
           return ResponseEntity.ok(userService.createUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public String login(@RequestBody UserDTO userDTO) throws Exception {
        try{
            return userService.login(userDTO);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    @GetMapping("/")
    public String oAuth2Test(){
        return "abc";
    }
}

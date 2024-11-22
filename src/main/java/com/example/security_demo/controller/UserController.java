package com.example.security_demo.controller;

import com.example.security_demo.dtos.userDtos.LoginRequestDTO;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.dtos.userDtos.UpdateInforRequestDTO;
import com.example.security_demo.entity.User;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.exception.UserNotFoundException;
import com.example.security_demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO user) {
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
//    @GetMapping("/admin")
//       @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public String admin(){
//        return "admin";
//    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    @PutMapping("update/{userId}")
    public ResponseEntity<?> updateUserById(@Valid @PathVariable("userId") Long userId, @RequestBody UpdateInforRequestDTO updateInforRequestDTO){
        try {
            return ResponseEntity.ok( userService.updateUserInfo(userId, updateInforRequestDTO));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UserExistedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

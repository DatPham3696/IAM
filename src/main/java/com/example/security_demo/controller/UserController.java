package com.example.security_demo.controller;

import com.example.security_demo.dtos.userDtos.*;
import com.example.security_demo.entity.User;
import com.example.security_demo.exception.InvalidPasswordException;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.exception.UserNotFoundException;
import com.example.security_demo.service.EmailService;
import com.example.security_demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jdk.jfr.BooleanFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO user) {
        try{
           return ResponseEntity.ok(userService.signUp(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO userDTO, HttpServletRequest request) throws Exception {
        try{
            return userService.login(userDTO);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
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
    @PutMapping("changePassword/{userId}")
    public ResponseEntity<?> changeUserPassword(@PathVariable("userId") Long userId, @RequestBody ChangePasswordRequest changePasswordRequest){
        try {
            userService.changePassword(userId,changePasswordRequest);
            return ResponseEntity.ok("Change password successful");
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendResetPasswordRequest(@RequestParam String email){
        return ResponseEntity.ok(userService.forgotPasswordRequest(email));
    }
    @PostMapping("/resetPasswordToken")
    public ResponseEntity<?> restPasswordByToken(@RequestBody RetakePasswordByTokenDTO retakePasswordByTokenDTO){
        return ResponseEntity.ok(userService.resetPasswordByToken(retakePasswordByTokenDTO));
    }
    @PostMapping("/logoutAccount")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest){
        userService.logout(logoutRequest);
        return ResponseEntity.ok( userService.logout(logoutRequest));
    }
    @GetMapping("/confirmRegisterEmail")
    public String confirmEmail(@RequestParam String code){
        if(userService.confirmRegisterCode(code)){
            return "Confirm register email succesfull";
        }else {
            return "Invalid code";
        }
    }
    @GetMapping("/confirmLoginEmail")
    public ResponseEntity<?> confirmLoginEmail(@RequestParam String email, @RequestBody String code){
        return ResponseEntity.ok(userService.verifyLoginGenerateToken(email, code));
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(userService.refreshToken(request));
    }
//    @PostMapping(value = "uploads/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadImage(@PathVariable("userId") Long userId, @ModelAttribute("files") MultipartFile file) throws IOException {
//        User user = userService.getUById(userId);
//        String fileName = storeFile(file);
//        user.setProfilePicture(fileName);
//        userService.updateUser(user);
//        return ResponseEntity.ok(fileName);
//    }
//    private String storeFile(MultipartFile file) throws IOException {
//        String contentType = file.getContentType();
//        if (!contentType.startsWith("image/")) {
//            throw new IOException("Invalid file type. Only image files are allowed.");
//        }
//
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        String uniqueFilename = UUID.randomUUID().toString() + "_" + fileName;
//
//        java.nio.file.Path uploadDir = Paths.get("uploads");
//        if (!Files.exists(uploadDir)) {
//            Files.createDirectories(uploadDir);
//        }
//
//        java.nio.file.Path destination = uploadDir.resolve(uniqueFilename);
//        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//
//        return uniqueFilename;
//    }
}

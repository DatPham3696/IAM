package com.example.security_demo.controller;

import com.example.security_demo.dtos.userDtos.*;
import com.example.security_demo.exception.InvalidPasswordException;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.exception.UserNotFoundException;
import com.example.security_demo.service.EmailService;
import com.example.security_demo.service.UserKeycloakService;
import com.example.security_demo.service.DefaultUserService;
import com.example.security_demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final DefaultUserService defaultUserService;
    private final UserService userService;
    private final EmailService emailService;
    private final UserKeycloakService userKeycloakService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO user) {
        try {
            return ResponseEntity.ok(userService.register(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO userDTO, HttpServletRequest request) throws Exception {
        try {
            return defaultUserService.login(userDTO);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(defaultUserService.getUserById(userId));
    }

    @PutMapping("update/{userId}")
    public ResponseEntity<?> updateUserById(@Valid @PathVariable("userId") Long userId, @RequestBody UpdateInforRequestDTO updateInforRequestDTO) {
        try {
            return ResponseEntity.ok(defaultUserService.updateUserInfo(userId, updateInforRequestDTO));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UserExistedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("changePassword/{userId}")
    public ResponseEntity<?> changeUserPassword(@PathVariable("userId") Long userId, @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            defaultUserService.changePassword(userId, changePasswordRequest);
            return ResponseEntity.ok("Change password successful");
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendResetPasswordRequest(@RequestParam String email) {
        return ResponseEntity.ok(defaultUserService.forgotPasswordRequest(email));
    }

    @PostMapping("/resetPasswordToken")
    public ResponseEntity<?> restPasswordByToken(@RequestBody RetakePasswordByTokenDTO retakePasswordByTokenDTO) {
        return ResponseEntity.ok(defaultUserService.resetPasswordByToken(retakePasswordByTokenDTO));
    }

    //    @PostMapping("/logoutAccount")
//    public ResponseEntity<?> logout(@RequestParam("authorization") String authorizationHeader,@RequestParam("refresh_token") String refreshToken){
//        defaultUserService.logout(logoutRequest);
//        return ResponseEntity.ok( defaultUserService.logout(logoutRequest));
//    }
    @PostMapping("/logoutAccount")
    public ResponseEntity<?> logout(@RequestHeader("authorization") String authorizationHeader, @RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok(userService.logout(authorizationHeader, refreshToken));
    }

    @GetMapping("/confirmRegisterEmail")
    public String confirmEmail(@RequestParam String code) {
        if (defaultUserService.confirmRegisterCode(code)) {
            return "Confirm register email succesfull";
        } else {
            return "Invalid code";
        }
    }

    @GetMapping("/confirmLoginEmail")
    public ResponseEntity<?> confirmLoginEmail(@RequestParam String email, @RequestBody String code) {
        return ResponseEntity.ok(defaultUserService.verifyLoginGenerateToken(email, code));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutKcl(@RequestHeader("authorization") String authorizationHeader,
                                       @RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok().body(userKeycloakService.logout(authorizationHeader, refreshToken));
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

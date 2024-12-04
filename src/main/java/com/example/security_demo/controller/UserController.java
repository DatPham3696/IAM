package com.example.security_demo.controller;

import com.example.security_demo.dto.request.Page.SearchRequest;
import com.example.security_demo.dto.request.user.*;
import com.example.security_demo.dto.response.user.UserResponse;
import com.example.security_demo.dto.response.user.UsersResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
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

    @GetMapping("/confirm-login-email")
    public ResponseEntity<?> confirmLoginEmail(@RequestParam String email, @RequestBody String code) {
        return ResponseEntity.ok(defaultUserService.verifyLoginGenerateToken(email, code));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasPermission('USER','VIEW')")
    public ResponseEntity<?> getUserById(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(defaultUserService.getUserById(userId));
    }

    @PutMapping("update/{userId}")
    @PreAuthorize("hasPermission('USER','UPDATE')")
    public ResponseEntity<?> updateUserById(@Valid @PathVariable("userId") String userId, @RequestBody UpdateInforRequestDTO updateInforRequestDTO) {
        try {
            return ResponseEntity.ok(defaultUserService.updateUserInfo(userId, updateInforRequestDTO));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UserExistedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("change-password/{userId}")
    @PreAuthorize("hasPermission('USER','UPDATE')")
    public ResponseEntity<?> changeUserPassword(@PathVariable("userId") String userId, @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            defaultUserService.changePassword(userId, changePasswordRequest);
            return ResponseEntity.ok("Change password successful");
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/send-email")
    @PreAuthorize("hasPermission('USER','UPDATE')")
    public ResponseEntity<?> sendResetPasswordRequest(@RequestParam String email) {
        return ResponseEntity.ok(defaultUserService.forgotPasswordRequest(email));
    }

    @PostMapping("/reset-password-token")
    @PreAuthorize("hasPermission('USER','UPDATE')")
    public ResponseEntity<?> resetPasswordByToken(@RequestBody RetakePasswordByTokenDTO retakePasswordByTokenDTO) {
        return ResponseEntity.ok(defaultUserService.resetPasswordByToken(retakePasswordByTokenDTO));
    }

//        @PostMapping("/logoutAccount")
//    public ResponseEntity<?> logout(@RequestParam("authorization") String authorizationHeader,@RequestParam("refresh_token") String refreshToken){
//        defaultUserService.logout(logoutRequest);
//        return ResponseEntity.ok( defaultUserService.logout(logoutRequest));
//    }
    @PostMapping("/logout-account")
    public ResponseEntity<?> logout(@RequestHeader("authorization") String authorizationHeader, @RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok(userService.logout(authorizationHeader, refreshToken));
    }

    @GetMapping("/confirm-register-email")
    public String confirmEmail(@RequestParam("code") String code) {
        if (defaultUserService.confirmRegisterCode(code)) {
            return "Confirm register email succesfull";
        } else {
            return "Invalid code";
        }
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutKcl(@RequestHeader("authorization") String authorizationHeader,
                                       @RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok().body(userKeycloakService.logout(authorizationHeader, refreshToken));
    }

    @PostMapping("soft-delete/{userId}")
    @PreAuthorize("hasPermission('USER','UPDATE')")
    public ResponseEntity<?> softDeleted(@PathVariable("userId") String userId, @RequestBody SoftDeleteRequest request) {
        return ResponseEntity.ok().body(defaultUserService.deletedSoft(userId, request));
    }

    @PostMapping("/enable-user/{userId}")
    public ResponseEntity<?> enableUser(@RequestHeader("authorization") String authorizationHeader, @PathVariable("userId") String userId,
                                        @RequestBody EnableUserRequest request) {
        return ResponseEntity.ok().body(userService.enableUser(authorizationHeader, userId, request));
    }

    @PostMapping("/reset-password-sync/{userId}")
    public ResponseEntity<?> resetPassword(@RequestHeader("authorization") String accessToken, @PathVariable("userId") String userId,
                                           @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok().body(userService.resetPassword(accessToken, userId, request));
    }

    @PostMapping("/get-user-infor")
    @PreAuthorize("hasPermission('USER','VIEW')")
    public ResponseEntity<?> getUserInfor(@RequestHeader("authorization") String accessToken) {
        return ResponseEntity.ok().body(defaultUserService.getUserInfor(accessToken));
    }
    @GetMapping("/users-infor")
    @PreAuthorize("hasPermission('ADMIN','ADMIN')")
    public ResponseEntity<UsersResponse<UserResponse>> getUsers(@ModelAttribute SearchRequest request){
        return ResponseEntity.ok().body(defaultUserService.getUsers(request));
    }

}

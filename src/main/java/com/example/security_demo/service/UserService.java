package com.example.security_demo.service;

import com.example.security_demo.config.JwtTokenUtils;
import com.example.security_demo.dtos.userDtos.*;
import com.example.security_demo.entity.*;
import com.example.security_demo.exception.InvalidPasswordException;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.exception.UserNotFoundException;
import com.example.security_demo.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final EmailService emailService;
    private final IInvalidTokenRepository invalidTokenRepository;
    private final RedisService redisService;
    private final LogService logService;
    private final HttpServletRequest request;
    public UserResponseDTO signUp(RegisterDTO registerDTO) throws UserExistedException {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new UserExistedException("Email already exists");
        }
        Role roleUser = roleRepository.findByRoleName("ROLE_USER");
        if (roleUser == null) {
            throw new RuntimeException("Role not found: ROLE_USER");
        }
        String verificationCode = UUID.randomUUID().toString();
        User user = userRepository.save(User.builder()
                .userName(registerDTO.getUserName())
                .address(registerDTO.getAddress())
                .passWord(passwordEncoder.encode(registerDTO.getPassWord()))
                .email(registerDTO.getEmail())
                .dateOfBirth(registerDTO.getDateOfBirth())
                .verificationCode(verificationCode)
                .roleId(roleUser.getId())
                .phoneNumber(registerDTO.getPhoneNumber())
                .build());
        List<String> descriptions = rolePermissionRepository.findAllByRoleId(roleUser.getId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(Permission::getDescription)
                        .orElse("Unknown Permission"))
                .toList();
        String sub = "Confirm register account";
        String text = "Hello " + user.getUsername() + ",\n\n" +
                "Thank you for registering an account. Please click the link below to confirm your account:\n" +
                "http://localhost:8080/confirmemail?code=" + verificationCode;
        emailService.sendEmail(user.getEmail(), sub, text);
        return UserResponseDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(roleUser.getRoleName())
                .perDescription(descriptions)
                .build();
    }

    public boolean confirmRegisterCode(String code) {
        User user = userRepository.findByVerificationCode(code);
        if (user != null && !user.isEnable()) {
            user.setEnable(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String login(LoginRequestDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new RuntimeException("Invalid inforr"));
        if (!passwordEncoder.matches(userDTO.getPassWord(), user.getPassword())) {
            throw new RuntimeException("invalid infor");
        }
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
//        securityContextHolder.setAuthentication(authentication);
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);
        emailService.sendEmail(user.getEmail(), "Verify login code", "Please using this code to complete your login:\n "+
                "http://localhost:8080/confirmemail?code=" + verificationCode);
        redisService.saveStringToRedis(user.getEmail(), verificationCode, 5, TimeUnit.MINUTES);
        return "Please check your verify code in your email ";
    }

    public String verifyLoginGenerateToken(String email, String code) {
        String storeCode = redisService.getStringFromRedis(email);
        if (storeCode != null && storeCode.equals(code)) {
            redisService.deleteFromRedis(email);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid inforr"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContext securityContextHolder = SecurityContextHolder.getContext();
            securityContextHolder.setAuthentication(authentication);
            logService.saveLog(UserActivityLog.builder()
                            .action("LOGIN")
                            .browserId(request.getRemoteAddr())
                            .userId(user.getId())
                            .timestamp(LocalDateTime.now())
                    .build());
            return jwtTokenUtils.generateToken(user);
        } else {
            throw new RuntimeException("Invalid code");
        }
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponseDTO getUserById(Long userId) {
        log.info("inside method");
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Cant find user"));
        Role role = roleRepository.findById(user.getRoleId()).orElseThrow(() -> new RuntimeException("Cant find roleName"));
        String roleName = role.getRoleName();
        List<Long> permissionId = rolePermissionRepository.findAllByRoleId(role.getId()).stream().map(RolePermission::getPermissionId).toList();
        List<String> description = permissionRepository.findAllById(permissionId).stream().map(Permission::getDescription).toList();
        return UserResponseDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(roleName)
                .perDescription(description)
                .build();
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponseDTO updateUserInfo(Long userId, UpdateInforRequestDTO updateInforRequestDTO) throws UserNotFoundException, UserExistedException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setUserName(updateInforRequestDTO.getUserName());
        existingUser.setAddress(updateInforRequestDTO.getAddress());
        existingUser.setPhoneNumber(updateInforRequestDTO.getPhoneNumber());
        existingUser.setDateOfBirth(updateInforRequestDTO.getDateOfBirth());
        User updatedUser = userRepository.save(existingUser);
        Role roleUser = roleRepository.findById(updatedUser.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        List<String> descriptions = rolePermissionRepository.findAllByRoleId(roleUser.getId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(Permission::getDescription)
                        .orElse("Unknown Permission"))
                .toList();
        String sub = "Modify infor";
        String text = "Hello " + existingUser.getUsername() + ",\n\n" +
                "Modify information successfully";
        emailService.sendEmail(existingUser.getEmail(), sub, text);
        return UserResponseDTO.builder()
                .userName(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .address(updatedUser.getAddress())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .roleName(roleUser.getRoleName())
                .perDescription(descriptions)
                .build();
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest changePasswordRequest) throws InvalidPasswordException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Password not match");
        }
        user.setPassWord(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return ChangePasswordResponse.builder().email(user.getEmail()).build();
    }

    public String forgotPasswordRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        String token = jwtTokenUtils.generateToken(user);
        emailService.sendPasswordResetToken(email, token);
        return "Open gmail and check for our email to reset your password";
    }

    public String resetPasswordByToken(RetakePasswordByTokenDTO retakePasswordByTokenDTO) {
        String email = jwtTokenUtils.getSubFromToken(retakePasswordByTokenDTO.getToken());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        user.setPassWord(passwordEncoder.encode(retakePasswordByTokenDTO.getNewPassword()));
        userRepository.save(user);
        return "Change password successfully";
    }

    public String logout(LogoutRequest request) {
        InvalidToken invalidToken = InvalidToken.builder()
                .id(jwtTokenUtils.getJtiFromToken(request.getToken()))
                .expiryTime(jwtTokenUtils.getExpirationTimeFromToken(request.getToken()))
                .build();
        invalidTokenRepository.save(invalidToken);
        return "logout success";
    }
    public User getUById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}



























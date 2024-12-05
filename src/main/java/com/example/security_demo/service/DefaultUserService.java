package com.example.security_demo.service;

import com.example.security_demo.config.JwtTokenUtils;
import com.example.security_demo.dto.request.Page.SearchRequest;
import com.example.security_demo.dto.request.user.*;
import com.example.security_demo.dto.response.user.ChangePasswordResponse;
import com.example.security_demo.dto.response.user.JwtResponse;
import com.example.security_demo.dto.response.user.UserResponse;
import com.example.security_demo.dto.response.user.UsersResponse;
import com.example.security_demo.entity.*;
import com.example.security_demo.enums.LogInfor;
import com.example.security_demo.exception.InvalidPasswordException;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.exception.UserNotFoundException;
import com.example.security_demo.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class DefaultUserService {
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
    private final IRoleUserRepository roleUserRepository;
    private final HttpServletRequest request;
    private final RefreshTokenService refreshTokenService;
    private final UserKeycloakService userKeycloakService;
    private final UserRepositoryImpl userRepositoryImpl;

    public UserResponse register(RegisterDTO registerDTO) throws UserExistedException {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new UserExistedException("Email already exists");
        }
        Role role = roleRepository.findByCode("USER").orElseThrow(() -> new RuntimeException("role not found"));
        if (role == null) {
            throw new RuntimeException("Role not found: USER");
        }
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);
        User user = userRepository.save(User.builder()
                .userName(registerDTO.getUserName())
                .keyclUserId(userKeycloakService.getKeycloakUserId(registerDTO))
                .address(registerDTO.getAddress())
                .passWord(passwordEncoder.encode(registerDTO.getPassWord()))
                .email(registerDTO.getEmail())
                .dateOfBirth(registerDTO.getDateOfBirth())
                .verificationCode(verificationCode)
                .phoneNumber(registerDTO.getPhoneNumber())
                .build());

        RoleUser roleUser = roleUserRepository.save(RoleUser.builder()
                .roleId(role.getId())
                .userId(user.getId())
                .build());

        List<String> descriptions = rolePermissionRepository.findAllByRoleId(role.getId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(Permission::getScope)
                        .orElse("Unknown Permission"))
                .toList();

        String sub = "Confirm register account";
        String text = "Hello " + user.getUsername() + ",\n\n" +
                "Thank you for registering an account. Please click the link below to confirm your account:\n" +
                "http://localhost:8080/confirmemail?code=" + verificationCode;
        emailService.sendEmail(user.getEmail(), sub, text);
        return UserResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(role.getCode())
                .perDescription(descriptions)
                .build();
    }

    public boolean confirmRegisterCode(String code) {
        User user = userRepository.findByVerificationCode(code);
        if (user != null && !user.isEmailVerified()) {
            user.setEmailVerified(true);
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
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);
        emailService.sendEmail(user.getEmail(), "Verify login code", "Please using this code to complete your login:\n " +
                "http://localhost:8080/confirmemail?code=" + verificationCode);
        redisService.saveStringToRedis(user.getEmail(), verificationCode, 5, TimeUnit.MINUTES);
        return "Please check your verify code in your email ";
    }

    public JwtResponse verifyLoginGenerateToken(String email, String code) {
        String storeCode = redisService.getStringFromRedis(email);
        if (storeCode != null && storeCode.equals(code)) {
            redisService.deleteFromRedis(email);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid inforr"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContext securityContextHolder = SecurityContextHolder.getContext();
            securityContextHolder.setAuthentication(authentication);

            logService.saveLog(UserActivityLog.builder()
                    .action(LogInfor.LOGIN.getDescription())
                    .browserId(request.getRemoteAddr())
                    .userId(user.getId())
                    .timestamp(LocalDateTime.now())
                    .build());
            refreshTokenService.deleteByUserId(user.getId());
            String token = jwtTokenUtils.generateToken(user);
            return JwtResponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshTokenService.createRefreshToken(user.getId(),
                            jwtTokenUtils.getJtiFromToken(token),
                            jwtTokenUtils.getExpirationTimeFromToken(token)).getRefreshToken())
                    .build();
        } else {
            throw new RuntimeException("Invalid code");
        }
    }

    // load token mới mỗi lần refresh
    //    public JwtResponse refreshToken(RefreshTokenRequest request) {
//        Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
//        if (refreshToken.isPresent()) {
//            invalidTokenRepository.save(
//                    InvalidToken.builder()
//                            .id(refreshToken.get().getAccessTokenId())
//                            .build());
//            RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken.get());
//            User user = userRepository.findById(validRefreshToken.getUserId()).orElseThrow(() -> new RuntimeException("Not find user"));
//            refreshTokenService.deleteByUserId(user.getId());
//            String accessToken = jwtTokenUtils.generateToken(user);
//            return JwtResponse.builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshTokenService.createRefreshToken(user.getId(),jwtTokenUtils.getJtiFromToken(accessToken)).getToken())
//                    .build();
//        }
//        throw new RuntimeException("Error");
//    }
    // dùng refresh đến khi hết hạn
    public String refreshToken(RefreshTokenRequest request) {
        Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        if (refreshToken.isPresent()) {
            invalidTokenRepository.save(
                    InvalidToken.builder()
                            .id(refreshToken.get().getAccessTokenId())
                            .expiryTime(refreshToken.get().getAccessTokenExp())
                            .build());
            RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken.get());
            User user = userRepository.findById(validRefreshToken.getUserId()).orElseThrow(() -> new RuntimeException("Not find user"));
            String accessToken = jwtTokenUtils.generateToken(user);
            return accessToken;
        }
        throw new RuntimeException("Error");
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Cant find user"));
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        Role role = roleRepository.findById(roleUser.getRoleId()).orElseThrow(() -> new RuntimeException("Cant find roleName"));
        String roleName = role.getCode();
        List<Long> permissionId = rolePermissionRepository.findAllByRoleId(role.getId()).stream().map(RolePermission::getPermissionId).toList();
        List<String> description = permissionRepository.findAllById(permissionId).stream().map(Permission::getScope).toList();
        return UserResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(roleName)
                .perDescription(description)
                .build();
    }

    public UserResponse updateUserInfo(String userId, UpdateInforRequestDTO updateInforRequestDTO) throws UserNotFoundException,
            UserExistedException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setUserName(updateInforRequestDTO.getUserName());
        existingUser.setAddress(updateInforRequestDTO.getAddress());
        existingUser.setPhoneNumber(updateInforRequestDTO.getPhoneNumber());
        existingUser.setDateOfBirth(updateInforRequestDTO.getDateOfBirth());
        User updatedUser = userRepository.save(existingUser);

        RoleUser roleUser = roleUserRepository.findByUserId(updatedUser.getId());
        Role role = roleRepository.findById(roleUser.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<String> descriptions = rolePermissionRepository.findAllByRoleId(role.getId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(Permission::getScope)
                        .orElse("Unknown Permission"))
                .toList();

        String sub = "Modify infor";
        String text = "Hello " + existingUser.getUsername() + ",\n\n" +
                "Modify information successfully";
        emailService.sendEmail(existingUser.getEmail(), sub, text);

        logService.saveLog(UserActivityLog.builder()
                .action(LogInfor.UPDATE.getDescription())
                .browserId(request.getRemoteAddr())
                .userId(existingUser.getId())
                .timestamp(LocalDateTime.now())
                .build());

        return UserResponse.builder()
                .userName(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .address(updatedUser.getAddress())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .roleName(role.getCode())
                .perDescription(descriptions)
                .build();
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public ChangePasswordResponse changePassword(String userId, ChangePasswordRequest changePasswordRequest) throws InvalidPasswordException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Password not match");
        }
        user.setPassWord(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        logService.saveLog(UserActivityLog.builder()
                .action(LogInfor.CHANGEPASSWORD.getDescription())
                .browserId(request.getRemoteAddr())
                .userId(user.getId())
                .timestamp(LocalDateTime.now())
                .build());
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
        logService.saveLog(UserActivityLog.builder()
                .action(LogInfor.RESETPASSWORD.getDescription())
                .browserId(request.getRemoteAddr())
                .userId(user.getId())
                .timestamp(LocalDateTime.now())
                .build());
        return "Change password successfully";
    }

    public String logout(String accessToken, String refreshToken) {
        if (accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7).trim();
        }
        InvalidToken invalidToken = InvalidToken.builder()
                .id(jwtTokenUtils.getJtiFromToken(accessToken))
                .expiryTime(jwtTokenUtils.getExpirationTimeFromToken(accessToken))
                .refreshTokenId(jwtTokenUtils.getJtiFromToken(refreshToken))
                .build();
        invalidTokenRepository.save(invalidToken);
        return "logout success";
    }

    @Transactional
    public String deletedSoft(String userId, SoftDeleteRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not find user"));
        boolean status = request.isStatus();
        user.setDeleted(status);
        userRepository.save(user);
        return "User updated";
    }

    public String enableUser(String userId, EnableUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not find user"));
        boolean enabled = request.isEnabled();
        user.setEnabled(enabled);
        userRepository.save(user);
        return "User updated";
    }

    public String resetPassword(String userId, ResetPasswordRequest request) {
        User user = userRepository.findByKeyclUserId(userId);
        user.setPassWord(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Reset password successfully";
    }

    public UserResponse getUserInfor(String token) {
        if (token.startsWith("Bearer")) {
            token = token.substring(7).trim();
        }
        User user = userRepository.findByEmail(jwtTokenUtils.getSubFromToken(token)).get();
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        Role role = roleRepository.findById(roleUser.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        List<String> descriptions = rolePermissionRepository.findAllByRoleId(role.getId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(Permission::getScope)
                        .orElse("Unknown Permission"))
                .toList();
        return UserResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(role.getCode())
                .perDescription(descriptions)
                .build();
    }

    public UsersResponse<UserResponse> getUsers(SearchRequest request) {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, request.getAttribute());//

        if ("desc".equalsIgnoreCase(request.getSort())) {
            order = new Sort.Order(Sort.Direction.DESC, request.getAttribute());
        }
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(order));
        Page<User> userPage = (request.getKeyword() == null || request.getKeyword().isBlank()) ? userRepository
                .findAll(sortedPageable) :
                userRepository.findByKeyWord(request.getKeyword().trim(), sortedPageable);
        List<UserResponse> userResponseDTOList = userPage.getContent().stream().map(user -> UserResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .build()).toList();
        return new UsersResponse<>(userResponseDTOList, userPage.getTotalPages());
    }

    public UsersResponse<UserResponse> getUsers(UserSearchRequest request) {
        List<User> users = userRepositoryImpl.searchUser(request);
        List<UserResponse> userResponseList = users.stream().map(user -> UserResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .build()).toList();
        return new UsersResponse<>(userResponseList, request.getPage());
    }
}



























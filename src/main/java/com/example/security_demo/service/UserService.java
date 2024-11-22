package com.example.security_demo.service;

import com.example.security_demo.config.JwtTokenUtils;
import com.example.security_demo.dtos.userDtos.LoginRequestDTO;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.dtos.userDtos.UpdateInforRequestDTO;
import com.example.security_demo.dtos.userDtos.UserResponseDTO;
import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RolePermission;
import com.example.security_demo.entity.User;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.exception.UserNotFoundException;
import com.example.security_demo.repository.IPermissionRepository;
import com.example.security_demo.repository.IRolePermissionRepository;
import com.example.security_demo.repository.IRoleRepository;
import com.example.security_demo.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public UserResponseDTO signUp(RegisterDTO registerDTO) throws UserExistedException {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new UserExistedException("Email already exists");
        }
        Role roleUser = roleRepository.findByRoleName("ROLE_USER");
        if (roleUser == null) {
            throw new RuntimeException("Role not found: ROLE_USER");
        }
        User user = userRepository.save(User.builder()
                .userName(registerDTO.getUserName())
                .address(registerDTO.getAddress())
                .passWord(passwordEncoder.encode(registerDTO.getPassWord()))
                .email(registerDTO.getEmail())
                .dateOfBirth(registerDTO.getDateOfBirth())
                .roleId(roleUser.getId())
                .phoneNumber(registerDTO.getPhoneNumber())
                .build());
        List<String> descriptions = rolePermissionRepository.findAllByRoleId(roleUser.getId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                .map(Permission::getDescription)
                 .orElse("Unknown Permission"))
                .toList();
        return UserResponseDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(roleUser.getRoleName())
                .perDescription(descriptions)
                .build();
    }

    public String login(LoginRequestDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new RuntimeException("Invalid inforr"));
        if (!passwordEncoder.matches(userDTO.getPassWord(), user.getPassword())) {
            throw new RuntimeException("invalid infor");
        };
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        securityContextHolder.setAuthentication(authentication);
        return jwtTokenUtils.generateToken(user);
    }
    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponseDTO getUserById(Long userId){
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
        return UserResponseDTO.builder()
                .userName(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .address(updatedUser.getAddress())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .roleName(roleUser.getRoleName())
                .perDescription(descriptions)
                .build();
    }
}



























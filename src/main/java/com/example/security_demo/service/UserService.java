package com.example.security_demo.service;

import com.example.security_demo.config.JwtTokenUtils;
import com.example.security_demo.dtos.userDtos.LoginRequestDTO;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.dtos.userDtos.UserResponseDTO;
import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RolePermission;
import com.example.security_demo.entity.User;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.repository.IPermissionRepository;
import com.example.security_demo.repository.IRolePermissionRepository;
import com.example.security_demo.repository.IRoleRepository;
import com.example.security_demo.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;

    public UserResponseDTO signUp(RegisterDTO registerDTO) throws UserExistedException {
        Set<Role> roles = new HashSet<>(); //set permission
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new UserExistedException("Email existed");
        }
        Role roleUser = roleRepository.findByRoleName("ROLE_ADMIN");
        if (roleUser == null) {
            throw new RuntimeException("Role not found: ROLE_USER");
        }
        roles.add(roleUser);
        User user = User.builder()
                .userName(registerDTO.getUserName())
                .address(registerDTO.getAddress())
                .passWord(passwordEncoder.encode(registerDTO.getPassWord()))
                .email(registerDTO.getEmail())
                .dateOfBirth(registerDTO.getDateOfBirth())
                .roleId(roleUser.getId())
                .phoneNumber(registerDTO.getPhoneNumber())
                .build();
        User saveUser = userRepository.save(user);
        Role roleOfUser = roleRepository.findById(user.getRoleId()).orElseThrow();
        List<Long> permissionId = rolePermissionRepository.findAllByRoleId(roleOfUser.getId()).stream()
                .map(rolePermission -> rolePermission.getPermissionId()).collect(Collectors.toList());
        List<String> description = permissionRepository.findAllById(permissionId).stream().map(Permission::getDescription).collect(Collectors.toList());
        return UserResponseDTO.builder()
                .userName(saveUser.getUsername())
                .email(saveUser.getEmail())
                .address(saveUser.getAddress())
                .roleName(roleUser.getRoleName())
                .perDescription(description)
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

}

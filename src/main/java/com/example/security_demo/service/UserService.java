package com.example.security_demo.service;

import com.example.security_demo.config.JwtTokenUtils;
import com.example.security_demo.dtos.userDtos.RegisterDTO;
import com.example.security_demo.dtos.userDtos.UserResponseDTO;
import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.User;
import com.example.security_demo.exception.UserExistedException;
import com.example.security_demo.repository.IPermissionRepository;
import com.example.security_demo.repository.IRoleRepository;
import com.example.security_demo.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final IPermissionRepository permissionRepository;
    public UserResponseDTO signUp(RegisterDTO registerDTO) throws UserExistedException {
        Set<Role> roles = new HashSet<>(); //set permission
        if(userRepository.existsByEmail(registerDTO.getEmail())){
            throw new UserExistedException("Email existed");
        }
        Role roleUser = roleRepository.findByRoleName("ROLE_USER");
        Set<Permission> permissions = roleUser.getPermissions();
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
                .roles(roles)
                .phoneNumber(registerDTO.getPhoneNumber())
                .build();
        userRepository.save(user);
        return UserResponseDTO.fromUser(user);
    }
//    public String login(LoginRequestDTO userDTO){
//        User user = userRepository.findByUserName(userDTO.getUserName()).orElseThrow(()-> new RuntimeException("Invalid inforr"));
//        if(!passwordEncoder.matches(userDTO.getPassWord(),user.getPassword())){
//            throw new RuntimeException("invalid infor");
//        };
//        return jwtTokenUtils.generateToken(user);
//    }

}

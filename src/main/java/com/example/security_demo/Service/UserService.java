package com.example.security_demo.Service;

import com.example.security_demo.Config.JwtTokenUtils;
import com.example.security_demo.DTO.UserDTO;
import com.example.security_demo.Entity.Users;
import com.example.security_demo.Enum.Role;
import com.example.security_demo.Exception.UserExistedException;
import com.example.security_demo.Repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    public Users createUser(Users user) throws UserExistedException {
        if(userRepository.existsByUserName(user.getUsername())){
            throw new UserExistedException("User existed");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassWord(passwordEncoder.encode(user.getPassword()));
        List<String> role = new ArrayList<>();
        role.add(Role.SCOPE_ADMIN.name());
        user.setRoles(role);
        return userRepository.save(user);
    }
    public String login(UserDTO userDTO){
        Users user = userRepository.findByUserName(userDTO.getUserName()).orElseThrow(()-> new RuntimeException("Invalid inforr"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if(!passwordEncoder.matches(userDTO.getPassWord(),user.getPassword())){
            throw new RuntimeException("invalid infor");
        };
        return jwtTokenUtils.generateToken(user);
    }

}

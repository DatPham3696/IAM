package com.example.security_demo.service;

import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RoleUser;
import com.example.security_demo.entity.User;
import com.example.security_demo.repository.IRoleRepository;
import com.example.security_demo.repository.IRoleUserRepository;
import com.example.security_demo.repository.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class UserInforDetailService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IRoleUserRepository roleUserRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        String roleName = roleRepository.findById(roleUser.getRoleId()).map(Role::getCode).orElseThrow(()->new RuntimeException("role not found"));
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        authorities.add(authority);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}

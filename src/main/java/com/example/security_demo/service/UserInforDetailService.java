package com.example.security_demo.service;

import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RolePermission;
import com.example.security_demo.entity.RoleUser;
import com.example.security_demo.entity.User;
import com.example.security_demo.enums.EnumRole;
import com.example.security_demo.repository.*;
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
    private final IRolePermissionRepository rolePermissionRepository;
    private final IPermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        Role role = roleRepository.findById(roleUser.getRoleId()).orElseThrow(() -> new RuntimeException("role not found"));
        String roleName = roleRepository.findById(roleUser.getRoleId()).map(Role::getCode).orElseThrow(() -> new RuntimeException("role not found"));
        List<String> permissions = rolePermissionRepository.findAllByRoleId(roleUser.getRoleId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(permission -> permission.getResourceCode() + "_" + permission.getScope())
                        .orElse("Unknow permission"))
                .toList();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String permission : permissions){
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        if(role.isAdmin()){
            authorities.add(new SimpleGrantedAuthority(EnumRole.ADMIN.name()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
    public UserDetails loadUserByUserUserId(String keyClkId) throws UsernameNotFoundException {
        User user = userRepository.findByKeyclUserId(keyClkId);
        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        Role role = roleRepository.findById(roleUser.getRoleId()).orElseThrow(() -> new RuntimeException("role not found"));
        String roleName = roleRepository.findById(roleUser.getRoleId()).map(Role::getCode).orElseThrow(() -> new RuntimeException("role not found"));
        List<String> permissions = rolePermissionRepository.findAllByRoleId(roleUser.getRoleId()).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(permission -> permission.getResourceCode() + "_" + permission.getScope())
                        .orElse("Unknow permission"))
                .toList();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String permission : permissions){
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        if(role.isAdmin()){
            authorities.add(new SimpleGrantedAuthority(EnumRole.ADMIN.name()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}

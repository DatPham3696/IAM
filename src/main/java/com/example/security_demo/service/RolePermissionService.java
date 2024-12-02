package com.example.security_demo.service;

import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.RolePermission;
import com.example.security_demo.repository.IPermissionRepository;
import com.example.security_demo.repository.IRolePermissionRepository;
import com.example.security_demo.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    public RolePermission addRolePermission(String code, String resourceCode, String scope) {
        Role role = roleRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Not found role"));
        Permission permission = permissionRepository.findByResourceCodeAndScope(resourceCode, scope)
                .orElseThrow(() -> new RuntimeException("Not found permission"));
        boolean check = rolePermissionRepository.findAllByRoleId(role.getId()).stream()
                .anyMatch(rolePermission -> rolePermission.getPermissionId().equals(permission.getId()));
        if(check){
            throw new RuntimeException("Role permission existed");
        }
        return rolePermissionRepository.save(RolePermission.builder()
                .permissionId(permission.getId())
                .roleId(role.getId())
                .build());
    }
}

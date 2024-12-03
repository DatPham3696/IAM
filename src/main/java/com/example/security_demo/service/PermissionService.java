package com.example.security_demo.service;

import com.example.security_demo.entity.Permission;
import com.example.security_demo.repository.IPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final IPermissionRepository permissionRepository;
    public Permission addPermission(Permission permission){
        if(permissionRepository.existsByScopeAndResourceCode(permission.getScope(), permission.getResourceCode())){
            throw new IllegalArgumentException("data existed");
        }
        return permissionRepository.save(permission);
    }
    public void deletePermissionByResourceCodeAndScope(String resourceCode, String scope){
        Permission permission = permissionRepository.findByResourceCodeAndScope(resourceCode, scope).orElseThrow(() -> new RuntimeException("Not found"));
        permissionRepository.delete(permission);
    }
}

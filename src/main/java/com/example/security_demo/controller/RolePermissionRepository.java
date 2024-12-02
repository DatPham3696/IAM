package com.example.security_demo.controller;

import com.example.security_demo.entity.RolePermission;
import com.example.security_demo.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role-permission")
@RequiredArgsConstructor
public class RolePermissionRepository {
    private final RolePermissionService rolePermissionService;

    @PostMapping("/create-role-permission")
    public ResponseEntity<RolePermission> createRolePermission(@RequestParam("code") String code,
                                            @RequestParam("resource_code") String resourceCode,
                                            @RequestParam("scope") String scope ){
        return ResponseEntity.ok().body(rolePermissionService.addRolePermission(code,resourceCode, scope));
    }
}

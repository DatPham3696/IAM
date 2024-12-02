package com.example.security_demo.controller;

import com.example.security_demo.entity.Permission;
import com.example.security_demo.service.PermissionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/permission")
public class PermissionRepository {
    private final PermissionService permissionService;
    @PostMapping("/create-permission")
    public ResponseEntity<Permission> addPermission(@RequestBody Permission permission){
        return ResponseEntity.ok().body(permissionService.addPermission(permission));
    }
}

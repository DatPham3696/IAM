package com.example.security_demo.controller;

import com.example.security_demo.dto.request.role.SoftDeleteRoleRequest;
import com.example.security_demo.entity.Role;
import com.example.security_demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/create-role")
    @PreAuthorize("hasPermission('ROLE','CREATE')")
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        Role createdRole = roleService.addRole(role);
        return ResponseEntity.ok().body(createdRole);
    }

    @PostMapping("/soft-role-delete/{code}")
    @PreAuthorize("hasPermission('ROLE','DELETE')")
    public ResponseEntity<?> softDelete(@PathVariable("roleId") String code,@RequestBody SoftDeleteRoleRequest request){
        return ResponseEntity.ok().body(roleService.softDelete(code,request));
    }

    @GetMapping("roles-paging")
    @PreAuthorize("hasPermission('ROLE','VIEW')")
    public ResponseEntity<?> getRoles(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok().body(roleService.getRoles(pageable));
    }
}

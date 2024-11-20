package com.example.security_demo.dtos.roleDtos;

import com.example.security_demo.dtos.PermissionDtos.PermissionResponseDTO;
import com.example.security_demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDTO {
    private String roleName;
    private Set<PermissionResponseDTO> permission;
    public static RoleResponseDTO fromRole(Role role){
        return RoleResponseDTO.builder()
                .roleName(role.getRoleName())
                .permission(role.getPermissions().stream().map(PermissionResponseDTO::fromPermission).collect(Collectors.toSet()))
                .build();
    }
}

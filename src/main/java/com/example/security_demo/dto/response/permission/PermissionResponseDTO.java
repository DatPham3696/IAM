package com.example.security_demo.dto.response.permission;


import com.example.security_demo.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponseDTO {
    private String description;
    public static PermissionResponseDTO fromPermission(Permission permission){
        return PermissionResponseDTO.builder()
                .description(permission.getScope())
                .build();
    }
}

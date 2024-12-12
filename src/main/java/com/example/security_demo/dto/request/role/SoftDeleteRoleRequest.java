package com.example.security_demo.dto.request.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoftDeleteRoleRequest {
    private boolean status;
}

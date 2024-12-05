package com.example.security_demo.dto.request.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftDeletePermissionRequest {
    private boolean status;
}

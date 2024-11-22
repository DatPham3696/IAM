package com.example.security_demo.dtos.userDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetakePasswordByTokenDTO {
    private String token;
    private String newPassword;
}

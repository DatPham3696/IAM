package com.example.security_demo.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @Email(message = "Invalid email format")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String passWord;
}

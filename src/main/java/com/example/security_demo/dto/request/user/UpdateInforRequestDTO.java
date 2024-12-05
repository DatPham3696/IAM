package com.example.security_demo.dto.request.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInforRequestDTO {
    @NotBlank(message = "Username is required")
    private String userName;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String passWord;
    @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Invalid phone number")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String address;
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}

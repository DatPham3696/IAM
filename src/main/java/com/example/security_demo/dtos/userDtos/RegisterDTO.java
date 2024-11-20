package com.example.security_demo.dtos.userDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    private String email;
    private String userName;
    private String passWord;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
}

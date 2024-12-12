package com.example.security_demo.dto.response.user;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Component
public class UserResponse {
    private String userName;
    private String email;
    private String address;
    private String roleName;
    private LocalDate dateOfBirth;
    private List<String> perDescription;
}

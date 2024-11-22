package com.example.security_demo.dtos.userDtos;

import com.example.security_demo.entity.Permission;
import com.example.security_demo.entity.Role;
import com.example.security_demo.entity.User;
import com.example.security_demo.repository.IRoleRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Component
public class UserResponseDTO {
    private String userName;
    private String email;
    private String address;
    private String roleName;
    private LocalDate dateOfBirth;
    private List<String> perDescription;
}

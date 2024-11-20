package com.example.security_demo.dtos.userDtos;

import com.example.security_demo.dtos.roleDtos.RoleResponseDTO;
import com.example.security_demo.entity.User;
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
public class UserResponseDTO {
    private String userName;
    private String email;
    private String address;
    private Set<RoleResponseDTO> roles;
    public static UserResponseDTO fromUser(User user){
        return UserResponseDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .roles(user.getRoles().stream().map(RoleResponseDTO::fromRole).collect(Collectors.toSet()))
                .build();
    }
}

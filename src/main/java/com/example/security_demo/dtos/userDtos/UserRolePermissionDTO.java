package com.example.security_demo.dtos.userDtos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

public class UserRolePermissionDTO {
    private String userName;
    private String email;
    private String roleName;
    private String permissionDescription;
    public UserRolePermissionDTO(String userName,String email,String roleName, String permissionDescription){
        this.userName = userName;
        this.email = email;
        this.roleName = roleName;
        this.permissionDescription = permissionDescription;
    }
}

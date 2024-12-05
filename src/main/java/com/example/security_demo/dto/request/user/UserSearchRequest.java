package com.example.security_demo.dto.request.user;

import com.example.security_demo.dto.request.Page.SearchRequest;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest extends SearchRequest {
    private String userName;
    @Email(message = "Invalid email format")
    private String email;
}

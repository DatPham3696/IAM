package com.example.security_demo.dtos.identity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationParam {
    private String username;
    private boolean enabled;
    private String email;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    List<Credential> credentials;
}

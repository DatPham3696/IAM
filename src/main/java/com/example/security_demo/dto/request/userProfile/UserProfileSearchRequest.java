package com.example.security_demo.dto.request.userProfile;

import com.example.security_demo.dto.request.Page.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileSearchRequest extends SearchRequest {
    private String userName;
    private String province;
}

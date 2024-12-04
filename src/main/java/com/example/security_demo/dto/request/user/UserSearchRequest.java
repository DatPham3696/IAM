package com.example.security_demo.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private int pageIndex = 1;

    private int pageSize = 10;

    private String keyword;
    private String type;

    private List<String> statuses;
    private String sortBy;
}

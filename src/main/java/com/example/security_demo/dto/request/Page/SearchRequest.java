package com.example.security_demo.dto.request.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String keyword;
    private int page = 0;
    private int size = 10;
    private String sort = "asc";
    private String attribute;
}

package com.example.security_demo.dto.request.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest{
    private String keyword;
    private int page = 1;
    private int size = 3;
    private String sort;
    private String attribute;
}

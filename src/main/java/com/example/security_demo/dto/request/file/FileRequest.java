package com.example.security_demo.dto.request.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest extends SearchRequest{
    private String fileName;
    private String fileType;
    private Instant createdDate;
    private String lastModifiedDate;
    private String createdBy;
}

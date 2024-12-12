package com.example.security_demo.service.eiDataService;

import io.jsonwebtoken.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

public class FileUtils {
    public static MultipartFile convertToMultipartFile(ByteArrayInputStream stream, String filename) throws IOException {
        byte[] content = stream.readAllBytes();
        return new CustomMultipartFile(content, filename);
    }
}

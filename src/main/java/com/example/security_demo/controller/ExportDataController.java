package com.example.security_demo.controller;

import com.example.security_demo.dto.request.userProfile.UserProfileSearchRequest;
import com.example.security_demo.service.eiDataService.ExportDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("api/export-data")
@RequiredArgsConstructor
public class ExportDataController {
    private final ExportDataService exportDataService;
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportUserData(@ModelAttribute UserProfileSearchRequest request) throws IOException {
        ByteArrayInputStream byteArrayInputStream = exportDataService.exportUserData(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users_data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(byteArrayInputStream));
    }
    @GetMapping("/send-data-to-storage")
    public ResponseEntity<?> exportUserDataToStorage(@ModelAttribute UserProfileSearchRequest request) {
        try {
            Object result = exportDataService.exportDataToStorage(request);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error exporting and uploading file: " + e.getMessage());
        }
    }

}

package com.example.security_demo.controller;

import com.example.security_demo.dto.request.file.FileRequest;
import com.example.security_demo.service.UserKeycloakService;
import com.example.security_demo.service.keyCloakService.StorageServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("api/private/file")
@RestController
@RequiredArgsConstructor
public class UploadPrivateController {
    private final StorageServiceClient storageServiceClient;
    private final UserKeycloakService userKeycloakService;
    @GetMapping("/paging")
    @PreAuthorize("hasPermission('FILE','VIEW')")
    public ResponseEntity<?> getListPage(@ModelAttribute FileRequest fileRequest) {
        String CLIENT_TOKEN = "Bearer " + userKeycloakService.token().getAccessToken();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(storageServiceClient.getListPage(CLIENT_TOKEN, fileRequest).getBody());
    }
    @PostMapping("/upload-file")
    @PreAuthorize("hasPermission('FILE','CREATE')")
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file,
                                        @RequestParam("visibility") boolean visibility,
                                        @RequestParam("version") String version) {
        try {
            String CLIENT_TOKEN = "Bearer " + userKeycloakService.token().getAccessToken();
            return ResponseEntity.ok()
                    .body(storageServiceClient.uploadPrivateFile(CLIENT_TOKEN, file, visibility, version).getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }
}

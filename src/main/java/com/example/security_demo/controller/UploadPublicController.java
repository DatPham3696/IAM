package com.example.security_demo.controller;

import com.example.security_demo.service.UserKeycloakService;
import com.example.security_demo.service.keyCloakService.StorageServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("api/file")
@RestController
@RequiredArgsConstructor
public class UploadPublicController {
    private final StorageServiceClient storageServiceClient;
    private final UserKeycloakService userKeycloakService;

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file,
                                        @RequestParam("visibility") boolean visibility,
                                        @RequestParam("version") String version) {
        try {
            String CLIENT_TOKEN = "Bearer " + userKeycloakService.token().getAccessToken();
            return ResponseEntity.ok()
                    .body(storageServiceClient.uploadPublicFile(CLIENT_TOKEN, file, visibility, version).getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/get-content/{fileId}")
    public ResponseEntity<Resource> getContent(@PathVariable("fileId") String fileId) {
        String CLIENT_TOKEN = "Bearer " + userKeycloakService.token().getAccessToken();
        ResponseEntity<Resource> response = storageServiceClient.getContent(CLIENT_TOKEN, fileId);
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        String CLIENT_TOKEN = "Bearer " + userKeycloakService.token().getAccessToken();
        return ResponseEntity.ok().body(storageServiceClient.test(CLIENT_TOKEN).getBody());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> download(@PathVariable("fileId") String fileId) {
        String CLIENT_TOKEN = "Bearer " + userKeycloakService.token().getAccessToken();
        ResponseEntity<Resource> response = storageServiceClient.downloadFile(CLIENT_TOKEN, fileId);
        return ResponseEntity.ok()
                .headers(response.getHeaders())
                .body(response.getBody());
    }

}

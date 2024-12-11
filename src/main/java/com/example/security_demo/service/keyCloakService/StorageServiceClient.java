package com.example.security_demo.service.keyCloakService;

import com.example.security_demo.dto.request.file.FileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "storage-service", url = "http://localhost:8099")
public interface StorageServiceClient {
    //public
    @PostMapping(value = "/api/file/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadPublicFile(@RequestHeader("Authorization") String authorizationHeader,
                                       @RequestPart("file") MultipartFile file,
                                       @RequestParam("visibility") boolean visibility,
                                       @RequestParam("version") String version);

    @GetMapping(value = "/api/file/public/get-content/{fileId}")
    ResponseEntity<Resource> getContent(@RequestHeader("authorization") String authorizationHeader,
                                        @PathVariable("fileId") String id);

    @GetMapping(value = "/api/file/public/test")
    ResponseEntity<String> test(@RequestHeader("authorization") String authorizationHeader);

    @GetMapping(value = "/api/file/public/download/{fileId}")
    ResponseEntity<Resource> downloadFile(@RequestHeader("authorization") String authorizationHeader,
                                          @PathVariable("fileId") String fileId);

    // private
    @PostMapping(value = "/api/file/private/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadPrivateFile(@RequestHeader("Authorization") String authorization,
                                 @RequestPart("file") MultipartFile file,
                                 @RequestParam("visibility") boolean visibility,
                                 @RequestParam("version") String version);

    @GetMapping(value = "/api/file/private/files-search")
    ResponseEntity<?> getListPage(@RequestHeader("Authentication") String authenticationHeader,
                                  @ModelAttribute FileRequest fileRequest);



}

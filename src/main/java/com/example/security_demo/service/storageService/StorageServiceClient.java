package com.example.security_demo.service.storageService;

import com.example.security_demo.config.FeignClientConfiguration;
import com.example.security_demo.dto.request.file.FileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@FeignClient(name = "storage-service", url = "http://localhost:8099", configuration = FeignClientConfiguration.class)
public interface StorageServiceClient {
    //public
    @PostMapping(value = "/api/file/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadPublicFile(@RequestPart("file") MultipartFile file,
                                       @RequestParam("visibility") boolean visibility,
                                       @RequestParam("version") String version,
                                       @RequestParam("owner") String owner);

    @GetMapping(value = "/api/file/public/get-content/{fileId}")
    ResponseEntity<Resource> getContent(@PathVariable("fileId") String id);

    @GetMapping(value = "/api/file/public/test")
    ResponseEntity<String> test();

    @GetMapping(value = "/api/file/public/download/{fileId}")
    ResponseEntity<Resource> downloadFile(@PathVariable("fileId") String fileId);

    // private
    @PostMapping(value = "/api/file/private/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadPrivateFile(@RequestPart("file") MultipartFile file,
                                        @RequestParam("visibility") boolean visibility,
                                        @RequestParam("version") String version,
                                        @RequestParam("owner") String owner);

    @PostMapping(value = "/api/file/private/files-search")
    ResponseEntity<?> getListPage(@RequestBody FileRequest fileRequest);

    @GetMapping(value = "/api/file/private/view-image/{fileId}")
    ResponseEntity<byte[]> viewImage(@PathVariable("fileId") String fileId,
                                @RequestParam("width") Optional<Integer> width,
                                @RequestParam("height") Optional<Integer> height,
                                @RequestParam("ratio") Optional<Double> ratio);

}

package com.example.security_demo.controller;

import com.example.security_demo.service.eiDataService.ImportDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/import-data")
@RequiredArgsConstructor
public class ImportDataController {
    private final ImportDataService importDataService;
    @PostMapping("/import")
    public ResponseEntity<?> importData(@RequestParam("file") MultipartFile file){
        try{
            String result = importDataService.importUserFromExcel(file);
            if(result.contains("errors")){
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(result);
            }
            return ResponseEntity.ok().body(result);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error:" + e.getMessage());
        }
    }
}

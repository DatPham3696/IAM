package com.example.security_demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobleException(Exception e){
        System.err.println("Exception occured:" + e.getMessage());
        return new ResponseEntity<>("Internal server error: "+ e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGlobleRuntimeException(RuntimeException e){
        return new ResponseEntity<>("Runtime exception" + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

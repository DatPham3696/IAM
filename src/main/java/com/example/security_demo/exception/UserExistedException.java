package com.example.security_demo.exception;


public class UserExistedException extends Exception{
    public UserExistedException(String message){
        super(message);
    }
}

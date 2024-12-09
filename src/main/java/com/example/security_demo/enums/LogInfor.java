package com.example.security_demo.enums;

public enum LogInfor {
    LOGIN("user login"),
    UPDATE("user update information"),
    CHANGEPASSWORD("user change password"),
    RESETPASSWORD("user reset password");
    private String description;
    LogInfor(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }
}

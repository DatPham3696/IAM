package com.example.security_demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;
    public void sendPasswordResetToken(String email, String token){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setText("Your token to changePassword" + token);
        emailSender.send(message);
    }
    public void sendEmail(String email, String sub, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(sub);
        message.setText(text);
        emailSender.send(message);
    }

}

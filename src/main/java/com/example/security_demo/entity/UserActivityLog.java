package com.example.security_demo.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
@Entity
@Table(name = "user_activity_log")
@Builder
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "action")
    private String action;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "browser_id")
    private String browserId;
}

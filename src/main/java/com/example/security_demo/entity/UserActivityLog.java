package com.example.security_demo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Table(name = "user_activity_log")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class UserActivityLog extends Auditable{
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

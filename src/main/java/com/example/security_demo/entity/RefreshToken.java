package com.example.security_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
    @Column(name = "expiry_date")
    private Date expiryDate;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "access_token_id")
    private String accessTokenId;
}

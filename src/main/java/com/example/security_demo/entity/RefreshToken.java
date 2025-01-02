package com.example.security_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh_token")
public class RefreshToken extends Auditable {
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
    @Column(name = "access_token_exp")
    private Date accessTokenExp;
}

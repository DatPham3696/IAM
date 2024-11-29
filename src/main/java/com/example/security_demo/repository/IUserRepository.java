package com.example.security_demo.repository;

import com.example.security_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUserName(String userName);
    Optional<User> findById(String userId);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User findByVerificationCode(String verificationCode);
    User findByKeyclUserId(String keycloakUserId);
}


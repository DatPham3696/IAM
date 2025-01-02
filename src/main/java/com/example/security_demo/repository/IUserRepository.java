package com.example.security_demo.repository;

import com.example.security_demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository // imple userRepocus
public interface IUserRepository extends JpaRepository<User, String> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUserName(String userName);

    Optional<User> findById(String userId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User findByVerificationCode(String verificationCode);

    User findByKeyclUserId(String keycloakUserId);

    @Query(value = "SELECT * FROM users u " +
            "WHERE unaccent(u.username || ' ' || u.email || ' ' || u.phone_number || ' ' || u.address) " +
            "ILIKE unaccent(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    Page<User> findByKeyWord(@Param("keyword") String keyword, Pageable pageable);
}


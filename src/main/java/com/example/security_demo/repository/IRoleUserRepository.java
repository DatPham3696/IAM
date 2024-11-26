package com.example.security_demo.repository;

import com.example.security_demo.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleUserRepository extends JpaRepository<RoleUser, Long> {
    RoleUser findByUserId(Long userId);
}

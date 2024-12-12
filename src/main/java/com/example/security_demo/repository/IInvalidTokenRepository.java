package com.example.security_demo.repository;

import com.example.security_demo.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvalidTokenRepository extends JpaRepository<InvalidToken,String> {
}

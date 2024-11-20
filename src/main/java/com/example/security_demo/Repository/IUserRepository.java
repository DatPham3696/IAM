package com.example.security_demo.Repository;

import com.example.security_demo.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.http.HttpResponse;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String userName);
    boolean existsByUserName(String userName);
}


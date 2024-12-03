package com.example.security_demo.repository;

import com.example.security_demo.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface IRoleUserRepository extends JpaRepository<RoleUser, Long> {
    RoleUser findByUserId(String userId);
    List<RoleUser> findAllByUserId(String userId);
//    Optional<String> find
}

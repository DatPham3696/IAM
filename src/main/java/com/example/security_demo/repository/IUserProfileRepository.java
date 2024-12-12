package com.example.security_demo.repository;

import com.example.security_demo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface IUserProfileRepository extends JpaRepository<UserProfile,String> {
    Optional<UserProfile> findByUsername(String username);
}

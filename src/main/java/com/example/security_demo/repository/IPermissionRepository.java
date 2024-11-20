package com.example.security_demo.repository;

import com.example.security_demo.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission,Long> {
    Permission findByDescription(String description);
}

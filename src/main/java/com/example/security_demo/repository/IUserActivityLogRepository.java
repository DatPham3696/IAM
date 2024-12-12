package com.example.security_demo.repository;

import com.example.security_demo.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserActivityLogRepository extends JpaRepository<UserActivityLog,Long> {
}

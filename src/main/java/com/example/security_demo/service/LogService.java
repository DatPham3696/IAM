package com.example.security_demo.service;

import com.example.security_demo.entity.UserActivityLog;
import com.example.security_demo.repository.IUserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    private final IUserActivityLogRepository userActivityLogRepository;

    public LogService(IUserActivityLogRepository userActivityLogRepository) {
        this.userActivityLogRepository = userActivityLogRepository;
    }
    public void saveLog(UserActivityLog userActivityLog){
        userActivityLogRepository.save(userActivityLog);
    }
}

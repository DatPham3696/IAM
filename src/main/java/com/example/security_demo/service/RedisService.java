package com.example.security_demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveStringToRedis(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public void saveStringToRedis(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    public String getStringFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public void deleteFromRedis(String key) {
        redisTemplate.delete(key);
    }
}

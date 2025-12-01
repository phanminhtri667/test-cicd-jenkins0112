package com.codewitharjun.fullstackbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void cacheAvatarUrl(Long userId, String url) {
        redisTemplate.opsForValue().set("avatar:" + userId, url, Duration.ofHours(1));
    }

    public String getAvatarUrl(Long userId) {
        return redisTemplate.opsForValue().get("avatar:" + userId);
    }
}

package com.notepad.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;

    public String getRefreshTokenById(Long id) {
        return redisTemplate.opsForValue().get(PREFIX + id);
    }

    public void saveRefreshToken(Long id, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(PREFIX + id, refreshToken, expiration);
    }

    public void deleteRefreshToken(Long id) {
        redisTemplate.delete(PREFIX + id);
    }
}

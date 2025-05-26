package com.yehyun.memo.notepad.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;

    public String getRefreshTokenByLoginId(String loginId) {
        return redisTemplate.opsForValue().get(PREFIX + loginId);
    }

    public void saveRefreshToken(String loginId, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(PREFIX + loginId, refreshToken, expiration);
    }

    public void deleteRefreshToken(String loginId) {
        redisTemplate.delete(PREFIX + loginId);
    }
}

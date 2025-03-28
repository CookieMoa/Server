package com.example.securitywithredis.global.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class RefreshUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh token을 Redis에 저장
    public void addRefreshToken(String username, String refreshToken, long expirationTimeInMillis) {
        redisTemplate.opsForValue().set(username, refreshToken, expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    // Refresh token을 Redis에서 삭제
    public void removeRefreshToken(String username) {
        redisTemplate.delete(username);
    }

    // Refresh token을 Redis에서 가져오기
    public String getRefreshToken(String username) {
        System.out.println("Received refresh token: " + redisTemplate.opsForValue().get(username));
        return redisTemplate.opsForValue().get(username);
    }
}

package com.chungnam.eco.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // 리프레시 토큰 저장
    public void saveRefreshToken(String username, String refreshToken) {
        // 기본 만료 시간(24시간)로 저장, 필요시 파라미터 추가 가능
        redisTemplate.opsForValue().set("refresh:" + username, refreshToken, 24, TimeUnit.HOURS);
    }

    // 리프레시 토큰 getter
    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh:" + username);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }

    // access 토큰 블랙리스트에 등록
    public void addToBlacklist(String accessToken, long expirationMillis) {
        // 토큰 값을 키로, 값은 의미 없고, 만료 시간 설정
        redisTemplate.opsForValue().set("blacklist:" + accessToken, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
    }

    // access 토큰이 블랙리스트에 포함됐는지 확인
    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey("blacklist:" + accessToken);
    }

    // 저장된 리프레시 토큰과 요청된 리프레시 토큰 일치 검증
    public boolean validateRefreshToken(String username, String refreshToken) {
        String stored = getRefreshToken(username);
        return refreshToken != null && refreshToken.equals(stored);
    }
}

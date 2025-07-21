package com.chungnam.eco.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                       @Value("${jwt.access-token-expiration:3600}") long accessTokenExpiration,
                       @Value("${jwt.refresh-token-expiration:1209600}") long refreshTokenExpiration) { // 14일
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration * 1000; // 초를 밀리초로 변환
        this.refreshTokenExpiration = refreshTokenExpiration * 1000;
    }

    /**
     * Access Token 생성 (1시간 유효)
     */
    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, role, accessTokenExpiration, "ACCESS");
    }

    /**
     * Refresh Token 생성 (14일 유효)
     */
    public String generateRefreshToken(Long userId, String role) {
        return generateToken(userId, role, refreshTokenExpiration, "REFRESH");
    }

    /**
     * 공통 토큰 생성 메서드
     */
    private String generateToken(Long userId, String role, long expiration, String tokenType) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    /**
     * Refresh Token의 만료 시간을 LocalDateTime으로 반환
     */
    public LocalDateTime getRefreshTokenExpiryDate() {
        return LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
    }

    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 타입 확인 (ACCESS or REFRESH)
     */
    public String getTokenType(String token) {
        try {
            return getClaims(token).get("type", String.class);
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * JWT 토큰에서 Claims 추출
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public Long getUserId(String token) {
        try {
            String subject = getClaims(token).getSubject();
            return Long.valueOf(subject);
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            throw new JwtException("토큰에 포함된 사용자 ID 형식이 올바르지 않습니다.");
        }
    }

    /**
     * JWT 토큰에서 사용자 권한 추출
     */
    public String getUserRole(String token) {
        try {
            return getClaims(token).get("role", String.class);
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }
}
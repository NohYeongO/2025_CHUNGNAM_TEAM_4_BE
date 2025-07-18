package com.chungnam.eco.common.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private final SecretKey key;
    private final long expiration;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * JWT 토큰 생성 (사용자 ID와 권한 포함)
     * @param userId 사용자 ID
     * @param role 사용자 권한
     * @return JWT 토큰 문자열
     */
    public String generateAccessToken(Long userId, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    /**
     * 사용자 정보로 리프레시 토큰 발급
     * @param userId 토큰에 포함할 userId
     * @param role 사용자 권한
     * @return JWT 리프레시 토큰(문자열)
     */
    public String createRefreshToken(Long userId, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰에서 Claims 추출
     *
     * @param token JWT 토큰
     * @return Claims 객체
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
     *
     * @param token JWT 토큰 (Bearer 접두사 제거된 순수 토큰)
     * @return 사용자 ID (Long)
     * @throws JwtException 토큰이 유효하지 않은 경우
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
     *
     * @param token JWT 토큰 (Bearer 접두사 제거된 순수 토큰)
     * @return 사용자 권한 (String)
     * @throws JwtException 토큰이 유효하지 않은 경우
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

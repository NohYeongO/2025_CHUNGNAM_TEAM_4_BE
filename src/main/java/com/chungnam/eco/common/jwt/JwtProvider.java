package com.chungnam.eco.common.jwt;


import com.chungnam.eco.common.exception.CustomException;
import com.chungnam.eco.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
     * QR Token 생성 (1분 유효)
     */
    public String generateQRToken(Long userId, String role) {
        return generateToken(userId, role, 1000 * 60, "QR");
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
     * Refresh Token의 만료 시간을 LocalDateTime 반환
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
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
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
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * JWT 토큰에서 사용자 권한 추출
     */
    public String getUserRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * 토큰이 곧 만료될 예정인지 확인 (만료 2초 전)
     */
    public boolean isTokenExpiringSoon(String token) {
        try{
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();

            // 2초 = 2,000밀리초 (5초 토큰에 맞게 조정)
            long twoSecondsInMs = 2 * 1000;
            long timeUntilExpiry = expiration.getTime() - now.getTime();

            return timeUntilExpiry <= twoSecondsInMs && timeUntilExpiry > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Access Token 만료 시간(초) 반환
     */
    public Long getAccessTokenValidityInSeconds() {
        return accessTokenExpiration / 1000;
    }
}

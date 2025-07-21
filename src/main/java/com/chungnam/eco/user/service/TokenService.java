package com.chungnam.eco.user.service;

import com.chungnam.eco.common.exception.InvalidTokenException;
import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.user.domain.RefreshToken;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    /**
     * 토큰 쌍 생성 (Access + Refresh)
     */
    @Transactional
    public TokenPair createTokenPair(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole().name());

        // 기존 Refresh Token이 있다면 업데이트, 없다면 새로 생성
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(existing -> {
                    existing.updateToken(refreshToken, jwtProvider.getRefreshTokenExpiryDate());
                    return existing;
                })
                .orElse(RefreshToken.builder()
                        .token(refreshToken)
                        .userId(user.getId())
                        .expiryDate(jwtProvider.getRefreshTokenExpiryDate())
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * Refresh Token으로 Access Token 갱신
     */
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
        }

        // 토큰 타입 확인
        if (!"REFRESH".equals(jwtProvider.getTokenType(refreshToken))) {
            throw new InvalidTokenException("Refresh Token이 아닙니다.");
        }

        // DB에서 Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh Token을 찾을 수 없습니다."));

        // 만료 검증
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new InvalidTokenException("만료된 Refresh Token입니다.");
        }

        // 새로운 Access Token 생성
        Long userId = jwtProvider.getUserId(refreshToken);
        String userRole = jwtProvider.getUserRole(refreshToken);

        return jwtProvider.generateAccessToken(userId, userRole);
    }

    /**
     * 로그아웃 - Refresh Token 삭제
     */
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * 만료된 Refresh Token 정리 (매일 자정 실행)
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("만료된 Refresh Token {} 개 삭제 완료", deletedCount);
    }

    /**
     * 토큰 쌍을 담는 내부 클래스
     */
    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
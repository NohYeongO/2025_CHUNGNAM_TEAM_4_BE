package com.chungnam.eco.user.service;

import com.chungnam.eco.common.exception.CustomException;
import com.chungnam.eco.common.exception.ErrorCode;
import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.user.domain.RefreshToken;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.RefreshTokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
     * Refresh Token 새로운 토큰 쌍 생성
     */
    @Transactional
    public Map<String, Object> refreshTokens(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰 타입 확인
        if (!"REFRESH".equals(jwtProvider.getTokenType(refreshToken))) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // DB Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 만료 검증
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 새로운 토큰 쌍 생성
        Long userId = jwtProvider.getUserId(refreshToken);
        String userRole = jwtProvider.getUserRole(refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(userId, userRole);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId, userRole);

        // 새로운 Refresh Token 업데이트
        storedToken.updateToken(newRefreshToken, jwtProvider.getRefreshTokenExpiryDate());
        refreshTokenRepository.save(storedToken);

        // 응답 데이터 준비
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("accessToken", newAccessToken);
        tokenData.put("refreshToken", newRefreshToken);
        tokenData.put("expiresIn", jwtProvider.getAccessTokenValidityInSeconds());

        return tokenData;
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
    @Getter
    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

    }
}
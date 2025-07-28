package com.chungnam.eco.common.security;

import com.chungnam.eco.common.exception.CustomException;
import com.chungnam.eco.common.exception.ErrorCode;
import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.user.domain.RefreshToken;
import com.chungnam.eco.user.repository.RefreshTokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 토큰 자동 갱신을 위한 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    /**
     * Access Token 만료 시 Refresh Token을 사용해서 자동으로 새 토큰을 발급
     * 
     * @param userId 사용자 ID (만료된 access token 추출)
     * @return 새로운 access token과 refresh token을 담은 TokenRefreshResult
     * @throws CustomException refresh token이 유효하지 않거나 만료된 경우
     */
    @Transactional
    public TokenRefreshResult refreshAccessToken(Long userId) {
        // 사용자의 Refresh Token 조회
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUserId(userId);
        
        if (refreshTokenOpt.isEmpty()) {
            log.warn("Refresh token not found for user: {}", userId);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken storedRefreshToken = refreshTokenOpt.get();

        // Refresh Token 만료 확인
        if (storedRefreshToken.isExpired()) {
            log.warn("Refresh token expired for user: {}", userId);
            // 만료된 토큰은 DB 삭제
            refreshTokenRepository.delete(storedRefreshToken);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // Refresh Token 자체의 JWT 유효성 검증
        String refreshTokenValue = storedRefreshToken.getToken();
        if (!jwtProvider.validateToken(refreshTokenValue)) {
            log.warn("Invalid refresh token for user: {}", userId);
            refreshTokenRepository.delete(storedRefreshToken);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰 타입 확인
        if (!"REFRESH".equals(jwtProvider.getTokenType(refreshTokenValue))) {
            log.warn("Token type mismatch for user: {}", userId);
            refreshTokenRepository.delete(storedRefreshToken);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 새로운 토큰 쌍 생성
        String userRole = jwtProvider.getUserRole(refreshTokenValue);
        String newAccessToken = jwtProvider.generateAccessToken(userId, userRole);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId, userRole);

        // Refresh Token 업데이트
        storedRefreshToken.updateToken(newRefreshToken, jwtProvider.getRefreshTokenExpiryDate());
        refreshTokenRepository.save(storedRefreshToken);

        log.info("Access token refreshed successfully for user: {}", userId);
        
        return new TokenRefreshResult(newAccessToken, newRefreshToken, userRole);
    }

    /**
     * 토큰 갱신 결과를 담는 클래스
     */
    @Getter
    public static class TokenRefreshResult {
        private final String accessToken;
        private final String refreshToken;
        private final String userRole;

        public TokenRefreshResult(String accessToken, String refreshToken, String userRole) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userRole = userRole;
        }

    }
}

package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TokenRefreshResponse {

    private final boolean success;
    private final String message;
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final Long expiresIn;
    private final LocalDateTime issuedAt;

    /**
     * 토큰 갱신 성공
     */
    public static TokenRefreshResponse success(String accessToken, String refreshToken, Long expiresIn) {
        return TokenRefreshResponse.builder()
                .success(true)
                .message("토큰이 성공적으로 갱신되었습니다.")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .issuedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 토큰 갱신 실패
     */
    public static TokenRefreshResponse failure(String message) {
        return TokenRefreshResponse.builder()
                .success(false)
                .message(message)
                .accessToken(null)
                .refreshToken(null)
                .tokenType(null)
                .expiresIn(null)
                .issuedAt(null)
                .build();
    }
}

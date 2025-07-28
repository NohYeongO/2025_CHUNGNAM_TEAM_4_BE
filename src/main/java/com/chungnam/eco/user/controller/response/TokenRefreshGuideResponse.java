package com.chungnam.eco.user.controller.response;

import lombok.Getter;

/**
 * 클라이언트 개발자를 위한 토큰 갱신 가이드 응답
 */
@Getter
public class TokenRefreshGuideResponse {
    
    private final String message;
    private final String integrationGuide;
    private final TokenHeaders headers;
    private final ErrorCodes errorCodes;

    public TokenRefreshGuideResponse(String integrationGuide) {
        this.message = "토큰 자동 갱신 시스템 통합 가이드";
        this.integrationGuide = integrationGuide;
        this.headers = new TokenHeaders();
        this.errorCodes = new ErrorCodes();
    }

    @Getter
    public static class TokenHeaders {
        private final String newAccessToken = "X-New-Access-Token";
        private final String newRefreshToken = "X-New-Refresh-Token";
        private final String tokenRefreshed = "X-Token-Refreshed";
        private final String description = "토큰이 자동 갱신될 때 응답 헤더에 포함되는 정보";
    }

    @Getter
    public static class ErrorCodes {
        private final String refreshTokenExpired = "A006 - 리프레시 토큰 만료";
        private final String invalidRefreshToken = "A007 - 유효하지 않은 리프레시 토큰";
        private final String tokenRefreshFailed = "A008 - 토큰 갱신 실패";
        private final String action = "위 에러 발생 시 로그인 페이지로 리다이렉트 필요";
    }
}

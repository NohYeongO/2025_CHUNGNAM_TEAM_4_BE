package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.common.security.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 토큰 자동 갱신 시스템 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/token-test")
@RequiredArgsConstructor
public class AuthTestController {

    private final JwtProvider jwtProvider;

    /**
     * 토큰 설정 정보 조회 API
     */
    @GetMapping("/token-info")
    public ResponseEntity<Map<String, Object>> getTokenInfo() {
        return ResponseEntity.ok(Map.of(
                "accessTokenValiditySeconds", jwtProvider.getAccessTokenValidityInSeconds(),
                "message", "현재 Access Token 만료 시간: " + jwtProvider.getAccessTokenValidityInSeconds() + "초",
                "warning", "현재 5초로 설정되어 토큰 자동 갱신 테스트용입니다.",
                "recommendation", "프로덕션 환경에서는 3600초(1시간) 이상으로 설정하세요."
        ));
    }

    /**
     * 인증이 필요한 테스트 API
     * 이 API를 호출해서 토큰 자동 갱신이 작동하는지 확인할 수 있습니다.
     */
    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint() {
        try {
            Long userId = AuthenticationHelper.getCurrentUserId();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "인증된 사용자입니다.",
                    "userId", userId,
                    "timestamp", System.currentTimeMillis(),
                    "note", "이 응답이 성공했다면 토큰이 유효하거나 자동 갱신되었습니다."
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "인증에 실패했습니다.",
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * 사용자 정보 조회 테스트 API
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            Long userId = AuthenticationHelper.getCurrentUserId();
            
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "message", "현재 로그인한 사용자 정보",
                    "authenticated", true
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "authenticated", false,
                    "message", "인증되지 않은 사용자입니다."
            ));
        }
    }
}

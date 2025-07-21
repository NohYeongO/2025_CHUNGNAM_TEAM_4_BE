package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    /**
     * Access Token 갱신 API
     * @param request Refresh Token이 포함된 요청
     * @return 새로운 Access Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Refresh Token이 필요합니다."
                ));
            }

            String newAccessToken = tokenService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "토큰이 갱신되었습니다.",
                    "accessToken", newAccessToken
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * 로그아웃 API (인증 필요)
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            Long userId = AuthenticationHelper.getCurrentUserId();
            tokenService.logout(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그아웃이 완료되었습니다."
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "로그아웃 처리 중 오류가 발생했습니다."
            ));
        }
    }
}
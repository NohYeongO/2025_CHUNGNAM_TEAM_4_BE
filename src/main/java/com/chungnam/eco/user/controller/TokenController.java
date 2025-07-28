package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.exception.CustomException;
import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.common.security.TokenRefreshHelper;
import com.chungnam.eco.user.controller.request.TokenRefreshRequest;
import com.chungnam.eco.user.controller.response.TokenRefreshResponse;
import com.chungnam.eco.user.controller.response.TokenRefreshGuideResponse;
import com.chungnam.eco.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    /**
     * Access Token 갱신 API
     * @param request Refresh Token이 포함된 요청
     * @return 새로운 Access Token과 Refresh Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            // 토큰 갱신 서비스 호출 (TokenService 새로운 액세스 토큰과 리프레시 토큰 반환)
            Map<String, Object> tokenData = tokenService.refreshTokens(refreshToken);
            
            String newAccessToken = (String) tokenData.get("accessToken");
            String newRefreshToken = (String) tokenData.get("refreshToken");
            Long expiresIn = (Long) tokenData.get("expiresIn");

            TokenRefreshResponse response = TokenRefreshResponse.success(
                    newAccessToken, 
                    newRefreshToken, 
                    expiresIn
            );

            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            TokenRefreshResponse response = TokenRefreshResponse.failure(e.getMessage());
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
            
        } catch (Exception e) {
            TokenRefreshResponse response = TokenRefreshResponse.failure("토큰 갱신 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 토큰 상태 확인 API
     * 클라이언트가 현재 토큰의 유효성을 확인할 수 있는 엔드포인트
     * @return 토큰 상태 정보
     */
    @GetMapping("/token/status")
    public ResponseEntity<Map<String, Object>> getTokenStatus() {
        try {
            // 현재 사용자 ID 조회 (토큰이 유효한 경우에만 성공)
            Long userId = AuthenticationHelper.getCurrentUserId();
            
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", userId,
                    "message", "토큰이 유효합니다."
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "valid", false,
                    "message", "토큰이 유효하지 않거나 만료되었습니다."
            ));
        }
    }

    /**
     * 개발자를 위한 토큰 갱신 시스템 통합 가이드 API
     * @return 클라이언트 통합 가이드
     */
    @GetMapping("/integration-guide")
    public ResponseEntity<TokenRefreshGuideResponse> getIntegrationGuide() {
        String guide = TokenRefreshHelper.getClientIntegrationGuide();
        TokenRefreshGuideResponse response = new TokenRefreshGuideResponse(guide);
        return ResponseEntity.ok(response);
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

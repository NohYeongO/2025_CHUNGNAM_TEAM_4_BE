package com.chungnam.eco.user.controller.jwtcontroller;

import com.chungnam.eco.user.jwt.JWTUtil;
import com.chungnam.eco.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    private final long ACCESS_EXPIRED = 1000 * 60 * 30; // 30분
    private final long REFRESH_EXPIRE = 1000 * 60 * 60 * 24; // 24시간

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username) {
        // 실제 상황에 따라 회원 DB에서 권한/role 정보 조회 필요
        String role = "USER"; // 예시 용, 실제 구현시 DB 조회 등 필요
        String accessCategory = "access";
        String refreshCategory = "refresh";

        String access = jwtUtil.createJwt(accessCategory, username, role, ACCESS_EXPIRED);
        String refresh = jwtUtil.createJwt(refreshCategory, username, role, REFRESH_EXPIRE);

        // 옵션: refresh 토큰을 서버/Redis 등에 저장
        tokenService.saveRefreshToken(username, refresh);

        // 응답 데이터 구성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", refresh);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        // 1. 블랙리스트/유효확인
        if (tokenService.isBlacklisted(refreshToken) || jwtUtil.isExpired(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        // 2. 토큰 파싱 및 사용자 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 3. 기존 refresh 토큰과 서버 저장된 토큰 일치 확인 (권장)
        if (!tokenService.validateRefreshToken(username, refreshToken)) {
            return ResponseEntity.status(401).body("Refresh token mismatch");
        }

        // 4. 새로운 AccessToken 및 필요 시 RefreshToken 발급
        String access = jwtUtil.createJwt("access", username, role, ACCESS_EXPIRED);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_EXPIRE);

        // 서버 측에 refresh 토큰 갱신
        tokenService.saveRefreshToken(username, newRefresh);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", newRefresh);

        return ResponseEntity.ok(tokens);
    }
}

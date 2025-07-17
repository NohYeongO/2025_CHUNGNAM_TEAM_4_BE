package com.chungnam.eco.user.controller;

import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.service.UserService;
import com.chungnam.eco.user.util.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController                                     // (1) 이 클래스가 REST API 컨트롤러임을 선언. JSON 등으로 HTTP 응답을 자동 직렬화.
@RequestMapping("/api/members")                      // (2) 모든 API 경로가 /api/members로 시작됨을 명시 (회원 관련 API 루트 경로)
public class UserController {

    // (3) 회원 비즈니스 로직 처리용 서비스, JWT(토큰) 처리용 유틸 클래스
    private final UserService userService;
    private final JwtProvider jwtProvider;

    // (4) 생성자 기반 의존성 주입. 서비스/유틸을 Spring이 자동으로 연결해줌.
    @Autowired
    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    /**
     * (5) 이메일 중복 여부 확인 엔드포인트
     * - GET /api/members/check-email?email=xxx
     * - 이미 등록된 이메일인지 true/false 반환
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        result.put("exists", userService.checkEmailDuplicate(email));
        return ResponseEntity.ok(result);
    }

    /**
     * (6) 회원가입 요청 처리
     * - POST /api/members/sign-up
     * - 이미 등록된 이메일이면 에러 반환, 아니면 새 회원 생성 후 id 응답
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        if (userService.checkEmailDuplicate(user.getEmail())) {
            result.put("error", "이미 등록된 이메일입니다.");        // 이메일 중복시 400 에러 반환
            return ResponseEntity.badRequest().body(result);
        }
        User newUser = userService.signUp(user);                  // 회원 데이터 저장 (비밀번호 암호화 포함)
        result.put("userId", newUser.getId());                    // 가입 성공하면 회원 id 반환
        return ResponseEntity.ok(result);
    }

    /**
     * (7) 로그인 및 JWT 토큰 발급
     * - POST /api/members/sign-in
     * - 이메일/비밀번호 일치 시 액세스/리프레시 토큰 반환, 틀리면 401 에러 반환
     */
    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String rawPw = req.get("password");
        return userService.validateUser(email, rawPw)
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    String accessToken = jwtProvider.createAccessToken(user);        // Access 토큰 생성
                    String refreshToken = jwtProvider.createRefreshToken(user);      // Refresh 토큰 생성
                    // jwtProvider.storeRefreshTokenRedis(email, refreshToken);     // (선택) Redis 방식 저장
                    jwtProvider.storeRefreshTokenDB(email, refreshToken);            // (기본) DB 방식 저장
                    map.put("accessToken", accessToken);
                    map.put("refreshToken", refreshToken);
                    return ResponseEntity.ok(map);                                   // 로그인 성공시 토큰 반환
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "인증 실패");                                  // 로그인 실패시 401 에러 반환
                    return ResponseEntity.status(401).body(error);
                });
    }

    /**
     * (8) 회원 포인트 정보 조회 (JWT 인증 필요)
     * - GET /api/members/point
     * - 헤더에서 JWT 토큰을 추출해 해당 사용자 포인트 반환
     */
    @GetMapping("/point")
    public ResponseEntity<Map<String, Object>> getPoint(@RequestHeader("Authorization") String bearer) {
        String token = bearer.replace("Bearer ", "");
        String email = jwtProvider.getEmail(token);                                   // 토큰에서 이메일 추출
        return userService.findByEmail(email)
                .map(user -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("point", user.getPoint());                             // 유저 포인트 반환
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "User not found");                             // 유저 미존재시 404 반환
                    return ResponseEntity.status(404).body(error);
                });
    }

    /**
     * (9) 액세스 토큰 재발급(Refresh 인증)
     * - POST /api/members/refresh-token
     * - 리프레시 토큰 제출시 유효성 확인, 만료 임박 시 새 리프레시 토큰까지 반환
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> req) {
        String refreshToken = req.get("refreshToken");
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Refresh Token Invalid");
            return ResponseEntity.status(401).body(error);
        }

        String email = jwtProvider.getEmail(refreshToken);                             // 토큰에서 유저 식별
        String newAccessToken = jwtProvider.createAccessTokenWithEmail(email);         // 새 Access 토큰 생성

        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", newAccessToken);

        // 만료 임박 시 새 RefreshToken 발급
        if (jwtProvider.isRefreshTokenExpiringSoon(refreshToken)) {
            String newRefreshToken = jwtProvider.createRefreshTokenWithEmail(email);
            // jwtProvider.storeRefreshTokenRedis(email, newRefreshToken);
            jwtProvider.storeRefreshTokenDB(email, newRefreshToken);
            map.put("refreshToken", newRefreshToken);                                 // 새 리프레시 토큰도 함께 반환
        }
        return ResponseEntity.ok(map);                                                // 성공시 새 토큰 반환
    }

    /**
     * (10) 로그아웃 (리프레시 토큰 삭제)
     * - POST /api/members/logout
     * - 서버 저장소(DB/Redis)에서 사용자의 리프레시 토큰 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        // jwtProvider.deleteRefreshTokenRedis(email);                                // Redis 방식 토큰 삭제
        jwtProvider.deleteRefreshTokenDB(email);                                      // DB 방식 토큰 삭제
        Map<String, Object> map = new HashMap<>();
        map.put("message", "로그아웃 성공");
        return ResponseEntity.ok(map);
    }
}

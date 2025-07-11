package com.chungnam.eco.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
// TODO: 회원 관련 API - 임시 TODO 세팅
public class UserController {

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email) {
        // TODO: 이메일 중복 여부 확인 로직
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody Object signUpRequest) {
        // TODO: 회원가입 처리 로직
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody Object signInRequest) {
        // TODO: 로그인 처리 로직 (JWT or 세션)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code) {
        // TODO: Google OAuth 콜백 처리
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find-id")
    public ResponseEntity<?> findUserId(@RequestBody Object findIdRequest) {
        // TODO: 이름, 전화번호 등으로 이메일 반환
        return ResponseEntity.ok("user@example.com");
    }

    @PostMapping("/verification/confirm")
    public ResponseEntity<?> verifyUser(@RequestBody Object verificationRequest) {
        // TODO: 본인 확인 로직 (이름, 이메일, 인증번호 등)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Object resetPasswordRequest) {
        // TODO: 새 비밀번호 저장 로직
        return ResponseEntity.ok().build();
    }

    @GetMapping("/main")
    public ResponseEntity<?> getMainPageInfo() {
        // TODO: 로그인된 사용자 정보 (닉네임, 보유 포인트)
        // + 오늘의 미션 목록(상태 포함) 조회 로직 구현
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-page/challenges")
    public ResponseEntity<?> getMyMissionParticipationHistory() {
        // TODO: 사용자가 참여한 모든 미션 + 상태(진행중, 심사중, 성공, 실패) 조회
        return ResponseEntity.ok().build();
    }
}

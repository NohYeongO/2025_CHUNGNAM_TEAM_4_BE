package com.chungnam.eco.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
// TODO: 관리자 전용 API - 임시 TODO 세팅
public class AdminChallengeController {

    @GetMapping("/challenges")
    public ResponseEntity<?> getMissionAuthList(
            @RequestParam(required = false) String status
    ) {
        // TODO: 사용자 미션 인증 요청 리스트 조회 로직 구현 (페이징, 상태 필터링)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/challenges/{challengeId}")
    public ResponseEntity<?> approveOrRejectChallenge(
            @PathVariable Long challengeId,
            @RequestParam("action") String action
    ) {
        // TODO: 승인/반려 처리 로직
        return ResponseEntity.ok().build();
    }
}

package com.chungnam.eco.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
// TODO: 관리자 전용 API - 임시 TODO 세팅
public class AdminController {

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

    @PostMapping("/missions")
    public ResponseEntity<?> createMission(@RequestBody Object missionRequest) {
        // TODO: 미션 생성 로직 (AI 활용 포함)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/missions/{missionId}/activate")
    public ResponseEntity<?> activateMission(@PathVariable Long missionId) {
        // TODO: 미션 활성화 처리
        return ResponseEntity.ok().build();
    }

    @GetMapping("/missions")
    public ResponseEntity<?> getAllMissions(@RequestParam(required = false) String status) {
        // TODO: 미션 리스트 조회 로직 (상태 필터링)
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/missions/{missionId}")
    public ResponseEntity<?> deleteMission(@PathVariable Long missionId) {
        // TODO: 미션 삭제 처리
        return ResponseEntity.noContent().build();
    }
}

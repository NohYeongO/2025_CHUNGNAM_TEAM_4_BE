package com.chungnam.eco.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/missions")
// TODO: 사용자 미션 관련 API - 임시 TODO 세팅
public class UserMissionController {

    @GetMapping
    public ResponseEntity<?> getMissionList(@RequestParam(required = false) String type) {
        // TODO: 미션 타입에 따른 목록 + 참여 상태 반환
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{missionId}")
    public ResponseEntity<?> getMissionDetail(@PathVariable Long missionId) {
        // TODO: missionId 기준으로 상세 정보 반환
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{missionId}/start")
    public ResponseEntity<?> startMission(@PathVariable Long missionId, @RequestParam Long memberId) {
        // TODO: 해당 사용자의 미션 참여 상태를 '진행중'으로 변경
        // 중복 참여 방지 로직 필요
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{missionId}/submit")
    public ResponseEntity<?> submitMission(
            @PathVariable Long missionId,
            @RequestParam Long memberId,
            @RequestBody Object missionSubmitRequest
    ) {
        // TODO: 텍스트/이미지 기반 미션 제출 처리
        return ResponseEntity.ok().build();
    }
}

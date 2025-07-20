package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
import com.chungnam.eco.user.service.UserAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class UserMissionController {

    private final UserAppService userAppService;

    /**
     * 미션 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<MissionListResponse> getMissionList() {
        Long userId = AuthenticationHelper.getCurrentUserId();
        return ResponseEntity.ok(userAppService.getMissionList(userId));
    }

    @GetMapping("/{missionId}")
    public ResponseEntity<MissionResponse> getMissionDetail(@PathVariable Long missionId) {
        return ResponseEntity.ok(userAppService.getMissionDetail(missionId));
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

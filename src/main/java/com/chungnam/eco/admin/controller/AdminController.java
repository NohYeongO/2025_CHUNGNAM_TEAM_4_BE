package com.chungnam.eco.admin.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    /**
     * 관리자 권한 테스트 API
     * @return 현재 사용자 정보 및 권한
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAdminAccess() {
        Long userId = AuthenticationHelper.getCurrentUserId();
        String role = AuthenticationHelper.getCurrentUserRole();
        boolean isAdmin = AuthenticationHelper.isAdmin();
        
        Map<String, Object> response = Map.of(
            "message", "관리자 API 접근 성공!",
            "userId", userId,
            "role", role,
            "isAdmin", isAdmin
        );
        
        return ResponseEntity.ok(response);
    }

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

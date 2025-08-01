package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.user.controller.request.MissionChoiceRequest;
import com.chungnam.eco.user.controller.request.MissionSubmitRequest;
import com.chungnam.eco.user.controller.response.MissionChoiceResponse;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
import com.chungnam.eco.user.controller.response.MissionSubmitResponse;
import com.chungnam.eco.user.service.UserAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 미션 선택 API
     * @param request 선택할 미션 ID 목록 (일일 3개, 주간 1개)
     * @return 미션 선택 결과
     */
    @PostMapping("/choice")
    public ResponseEntity<MissionChoiceResponse> missionChoice(@RequestBody @Valid MissionChoiceRequest request) {
        Long userId = AuthenticationHelper.getCurrentUserId();
        return ResponseEntity.ok(userAppService.chooseMissions(userId, request));
    }

    /**
     * 미션 제출 API
     * @param userMissionId, description
     * @param images 제출할 이미지 파일들 (1~2장)
     * @return 미션 제출 결과
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MissionSubmitResponse> submitMission(
            @RequestParam("userMissionId") Long userMissionId,
            @RequestParam("description") String description,
            @RequestPart("images") @NotNull(message = "이미지는 필수입니다.") 
            @Size(min = 1, max = 3, message = "이미지는 1~3장까지 업로드 가능합니다.") List<MultipartFile> images
    ) {
        Long userId = AuthenticationHelper.getCurrentUserId();

        MissionSubmitResponse response = userAppService.submitMission(userId, userMissionId, description, images);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}

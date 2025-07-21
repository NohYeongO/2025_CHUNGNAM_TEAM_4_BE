package com.chungnam.eco.admin.controller;

import com.chungnam.eco.admin.controller.request.RejectChallengeRequest;
import com.chungnam.eco.admin.controller.response.AllChallengeResponse;
import com.chungnam.eco.admin.controller.response.ChallengeDetailResponse;
import com.chungnam.eco.admin.service.AdminChallengeService;
import com.chungnam.eco.admin.service.dto.ChallengeDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminChallengeController {
    private final AdminChallengeService adminChallengeService;

    /**
     * 상태별로 Challenge 목록을 조회합니다.
     *
     * @param status   상태값 (PENDING, REJECTED, IN_PROGRESS, COMPLETED) - optional
     * @param pageable 페이징 정보 (기본: 0페이지, 10건, startedAt DESC)
     * @return 상태에 따른 Challenge 목록
     */
    @GetMapping("/challenges")
    public ResponseEntity<?> getMissionAuthList(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, page = 0, sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        List<ChallengeDto> challengeDtoList = adminChallengeService.getChallengeList(status, pageable);

        AllChallengeResponse response = AllChallengeResponse.success(challengeDtoList);

        return ResponseEntity.ok(response);
    }


    /**
     * 특정 Challenge의 상세 정보를 조회합니다.
     *
     * @param challengeId 조회할 Challenge의 ID
     * @return Challenge 상세 정보와 이미지 목록
     */
    @GetMapping("/challenges/{challengeId}")
    public ResponseEntity<?> getChallengeDetail(@PathVariable Long challengeId) {
        ChallengeDto challengeDto = adminChallengeService.getChallenge(challengeId);

        List<String> challengeImageList = adminChallengeService.getChallengeImages(challengeId);

        ChallengeDetailResponse response = ChallengeDetailResponse.success(challengeDto, challengeImageList);

        return ResponseEntity.ok(response);
    }


    /**
     * 특정 Challenge를 거절 처리합니다.
     *
     * @param challengeId            거절할 Challenge의 ID
     * @param rejectChallengeRequest 거절 사유가 담긴 요청 DTO
     */
    @PatchMapping("/challenges/{challengeId}/reject")
    public ResponseEntity<?> RejectChallenge(
            @PathVariable Long challengeId,
            @RequestBody RejectChallengeRequest rejectChallengeRequest
    ) {
        adminChallengeService.rejectChallenge(challengeId, rejectChallengeRequest.getReject_reason());
        return ResponseEntity.ok().build();
    }


    /**
     * 특정 Challenge를 승인 처리합니다.
     *
     * @param challengeId 승인할 Challenge의 ID
     */
    @PatchMapping("/challenges/{challengeId}/approve")
    public ResponseEntity<?> approveChallenge(
            @PathVariable Long challengeId
    ) {
        adminChallengeService.approveChallenge(challengeId);
        return ResponseEntity.ok().build();
    }
}

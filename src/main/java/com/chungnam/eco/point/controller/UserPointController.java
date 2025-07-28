package com.chungnam.eco.point.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.point.controller.response.PointHistoryResponse;
import com.chungnam.eco.point.service.PointService;
import com.chungnam.eco.point.service.dto.PointDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 포인트 관련 요청을 처리하는 REST 컨트롤러입니다.
 * - 포인트 사용
 * - 포인트 사용 내역 조회
 */
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class UserPointController {

    private final PointService pointService;

    /**
     * 현재 로그인된 사용자의 포인트 사용 내역을 조회하는 엔드포인트입니다.
     */
    @GetMapping("/history")
    public ResponseEntity<PointHistoryResponse> usedPoints() {
        Long userId = AuthenticationHelper.getCurrentUserId();

        Integer recentPoints = pointService.getUserPoint(userId);
        List<PointDto> pointDtoList = pointService.getPointHistory(userId);

        PointHistoryResponse response = PointHistoryResponse.from(recentPoints, pointDtoList);
        return ResponseEntity.ok(response);
    }
}

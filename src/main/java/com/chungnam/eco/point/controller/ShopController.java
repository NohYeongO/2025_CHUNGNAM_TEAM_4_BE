package com.chungnam.eco.point.controller;

import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.point.controller.request.UsedPointRequest;
import com.chungnam.eco.point.controller.response.UsedPointResponse;
import com.chungnam.eco.point.service.PayService;
import com.chungnam.eco.point.service.dto.PointDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class ShopController {
    private final PayService payService;
    private final JwtProvider jwtProvider;

    /**
     * 현재 로그인된 사용자가 포인트를 사용하는 엔드포인트입니다.
     *
     * @param request 사용자의 정보가 담긴 토큰
     */
    @PostMapping("/point")
    public ResponseEntity<UsedPointResponse> usedPoint(@Valid @RequestBody UsedPointRequest request) {
        Long userId = jwtProvider.getUserId(request.getToken());

        PointDto pointDto = payService.usedPoints(userId, request.getShop_name(), request.getPoints());

        UsedPointResponse response = UsedPointResponse.from(pointDto);
        return ResponseEntity.ok(response);
    }
}

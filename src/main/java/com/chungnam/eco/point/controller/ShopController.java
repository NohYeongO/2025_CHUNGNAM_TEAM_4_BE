package com.chungnam.eco.point.controller;

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

    /**
     * 현재 로그인된 사용자가 포인트를 사용하는 엔드포인트입니다.
     *
     * @param request 포인트 사용 요청 데이터 (가맹점 이름, 사용 포인트)
     */
    @PostMapping("/point")
    public ResponseEntity<UsedPointResponse> usedPoint(@Valid @RequestBody UsedPointRequest request) {

        PointDto pointDto = payService.usedPoints(request.getUserId(), request.getShop_name(), request.getPoints());

        UsedPointResponse response = UsedPointResponse.from(pointDto);
        return ResponseEntity.ok(response);
    }
}

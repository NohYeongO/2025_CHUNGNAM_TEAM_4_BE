package com.chungnam.eco.point.controller.response;

import com.chungnam.eco.point.service.dto.PointDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsedPointResponse {
    private String shop_name;
    private Integer used_points;
    private Integer total_points;

    public static UsedPointResponse from(PointDto pointDto) {
        return UsedPointResponse.builder()
                .shop_name(pointDto.getShopName())
                .used_points(pointDto.getUsedPoints())
                .total_points(pointDto.getTotalPoints())
                .build();

    }
}

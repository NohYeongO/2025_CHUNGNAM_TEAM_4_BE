package com.chungnam.eco.point.controller.response;

import com.chungnam.eco.point.service.dto.PointDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PointHistoryResponse {
    private final Integer Retention_points;
    private final List<PointHistory> point_history;

    @Builder
    @Getter
    public static class PointHistory {
        private final Integer used_points;
        private final String shop_name;
        private final Integer total_points;
        private final LocalDateTime payment_date;
    }

    public static PointHistoryResponse from(Integer Retention_points, List<PointDto> pointHistory) {

        List<PointHistory> pointHistoryList = pointHistory.stream()
                .map(v -> PointHistory.builder()
                        .used_points(v.getUsedPoints())
                        .total_points(v.getTotalPoints())
                        .shop_name(v.getShopName())
                        .payment_date(v.getCreatedAt())
                        .build())
                .toList();

        return PointHistoryResponse.builder()
                .Retention_points(Retention_points)
                .point_history(pointHistoryList)
                .build();
    }

}

package com.chungnam.eco.point.service.dto;

import com.chungnam.eco.point.domain.Point;
import com.chungnam.eco.user.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointDto {
    private Long id;
    private User user;
    private String shopName;
    private Integer usedPoints;
    private Integer totalPoints;
    private LocalDateTime createdAt;

    public static PointDto from(Point point) {
        return PointDto.builder()
                .id(point.getId())
                .user(point.getUser())
                .shopName(point.getShopName())
                .usedPoints(point.getUsedPoints())
                .totalPoints(point.getTotalPoints())
                .createdAt(point.getCreatedAt())
                .build();
    }
}

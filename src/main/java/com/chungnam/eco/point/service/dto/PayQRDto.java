package com.chungnam.eco.point.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayQRDto {
    private Long userId;
    private String payUrl;
    private LocalDateTime createdAt;
}

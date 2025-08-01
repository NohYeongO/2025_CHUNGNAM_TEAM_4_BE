package com.chungnam.eco.point.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsedPointRequest {
    private String token;
    private Integer points;
    private String shop_name;
}

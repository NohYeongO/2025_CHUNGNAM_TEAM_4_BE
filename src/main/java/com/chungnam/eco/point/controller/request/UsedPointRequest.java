package com.chungnam.eco.point.controller.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsedPointRequest {
    private final Long UserId;
    private final Integer points;
    private final String shop_name;
}

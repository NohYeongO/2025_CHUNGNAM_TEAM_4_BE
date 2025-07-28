package com.chungnam.eco.point.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayQRResponse {
    private final byte[] qrCode;

    public static PayQRResponse of(byte[] qrCode) {
        return PayQRResponse.builder()
                .qrCode(qrCode)
                .build();
    }
}

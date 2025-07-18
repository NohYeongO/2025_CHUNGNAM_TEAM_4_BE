package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCheckResponse {
    
    private final boolean exists;
    private final String message;
    
    /**
     * 이메일 사용 불가능 (중복됨)
     */
    public static EmailCheckResponse unavailable() {
        return EmailCheckResponse.builder()
                .exists(true)
                .message("이미 사용 중인 이메일입니다.")
                .build();
    }
    
    /**
     * 이메일 사용 가능
     */
    public static EmailCheckResponse available() {
        return EmailCheckResponse.builder()
                .exists(false)
                .message("사용 가능한 이메일입니다.")
                .build();
    }
}

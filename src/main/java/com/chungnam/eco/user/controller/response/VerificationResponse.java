package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationResponse {

    private final boolean success;
    private final String message;
    private final String verificationToken; // 비밀번호 재설정용 임시 토큰

    /**
     * 본인 확인 성공
     */
    public static VerificationResponse success(String verificationToken) {
        return VerificationResponse.builder()
                .success(true)
                .message("본인 확인이 완료되었습니다.")
                .verificationToken(verificationToken)
                .build();
    }

    /**
     * 본인 확인 실패
     */
    public static VerificationResponse failure(String message) {
        return VerificationResponse.builder()
                .success(false)
                .message(message)
                .verificationToken(null)
                .build();
    }
}


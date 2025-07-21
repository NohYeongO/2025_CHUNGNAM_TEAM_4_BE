package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetPasswordResponse {

    private final boolean success;
    private final String message;

    /**
     * 비밀번호 재설정 성공
     */
    public static ResetPasswordResponse success() {
        return ResetPasswordResponse.builder()
                .success(true)
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .build();
    }

    /**
     * 비밀번호 재설정 실패
     */
    public static ResetPasswordResponse failure(String message) {
        return ResetPasswordResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}

package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {
    
    private final boolean success;
    private final String message;
    private final String email;
    
    /**
     * 회원가입 성공
     */
    public static SignUpResponse success(String email) {
        return SignUpResponse.builder()
                .success(true)
                .message("회원가입이 완료되었습니다.")
                .email(email)
                .build();
    }
    
    /**
     * 회원가입 실패
     */
    public static SignUpResponse failure(String message) {
        return SignUpResponse.builder()
                .success(false)
                .message(message)
                .email(null)
                .build();
    }
}

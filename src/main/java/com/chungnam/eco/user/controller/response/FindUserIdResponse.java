package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUserIdResponse {
    
    private final boolean success;
    private final String message;
    private final String maskedEmail;
    
    /**
     * 아이디 찾기 성공
     */
    public static FindUserIdResponse success(String maskedEmail) {
        return FindUserIdResponse.builder()
                .success(true)
                .message("회원님의 아이디를 찾았습니다.")
                .maskedEmail(maskedEmail)
                .build();
    }
    
    /**
     * 아이디 찾기 실패 - 회원 없음
     */
    public static FindUserIdResponse notFound() {
        return FindUserIdResponse.builder()
                .success(false)
                .message("존재하지 않는 회원입니다.")
                .maskedEmail(null)
                .build();
    }
}

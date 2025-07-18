package com.chungnam.eco.user.controller.response;

import com.chungnam.eco.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {
    
    private final boolean success;
    private final String message;
    private final String accessToken;
    private final String email;
    private final String nickname;
    private final int point;
    private final String role;
    
    /**
     * 로그인 성공
     */
    public static SignInResponse success(String accessToken, User user) {
        return SignInResponse.builder()
                .success(true)
                .message("로그인이 완료되었습니다.")
                .accessToken(accessToken)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .point(user.getPoint())
                .role(user.getRole().name())
                .build();
    }
    
    /**
     * 로그인 실패
     */
    public static SignInResponse failure(String message) {
        return SignInResponse.builder()
                .success(false)
                .message(message)
                .accessToken(null)
                .email(null)
                .nickname(null)
                .point(0)
                .role(null)
                .build();
    }
}

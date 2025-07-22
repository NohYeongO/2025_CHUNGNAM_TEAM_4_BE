package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionSubmitResponse {
    
    private final boolean success;
    private final String message;
    private final Long challengeId;

    public static MissionSubmitResponse success(Long challengeId) {
        return MissionSubmitResponse.builder()
                .success(true)
                .message("미션이 성공적으로 제출되었습니다.")
                .challengeId(challengeId)
                .build();
    }
}

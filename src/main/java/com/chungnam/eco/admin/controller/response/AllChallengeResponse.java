package com.chungnam.eco.admin.controller.response;

import com.chungnam.eco.admin.service.dto.ChallengeDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AllChallengeResponse {
    private final List<ChallengeResponse> challenge_list;

    @Builder
    @Getter
    private static class ChallengeResponse {
        private Long challenge_id;
        private String user_nickname;
        private String mission_title;
        private String status;
    }

    public static AllChallengeResponse success(List<ChallengeDto> challengeDtoList) {
        List<ChallengeResponse> responses = challengeDtoList.stream()
                .map(dto -> ChallengeResponse.builder()
                        .challenge_id(dto.getId())
                        .user_nickname(dto.getUser().getNickname())
                        .mission_title(dto.getMission().getTitle())
                        .status(dto.getChallengeStatus().getDescription())
                        .build())
                .toList();

        return AllChallengeResponse.builder()
                .challenge_list(responses)
                .build();
    }
}

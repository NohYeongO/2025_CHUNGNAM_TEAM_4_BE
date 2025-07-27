package com.chungnam.eco.admin.controller.response;

import com.chungnam.eco.admin.service.dto.ChallengeDto;
import com.chungnam.eco.mission.domain.Mission;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllChallengeResponse {

    private final List<ChallengeResponse> challengeList;

    @Getter
    @Builder
    public static class ChallengeResponse {
        private Long challenge_id;
        private String user_nickname;
        private MissionInfo mission_info;
        private LocalDateTime created_at;
        private String status;
    }

    @Getter
    @Builder
    public static class MissionInfo {
        private String title;
        private String type;
        private String category;

        public static MissionInfo from(Mission mission) {
            return MissionInfo.builder()
                    .title(mission.getTitle())
                    .type(mission.getType().name())
                    .category(mission.getCategory().getDescription())
                    .build();
        }
    }

    public static AllChallengeResponse success(List<ChallengeDto> challengeDtoList) {
        List<ChallengeResponse> responses = challengeDtoList.stream()
                .map(dto -> ChallengeResponse.builder()
                        .challenge_id(dto.getId())
                        .user_nickname(dto.getUser().getNickname())
                        .mission_info(MissionInfo.from(dto.getMission()))
                        .created_at(dto.getCreatedAt())
                        .status(dto.getChallengeStatus().getDescription())
                        .build())
                .collect(Collectors.toList());

        return AllChallengeResponse.builder()
                .challengeList(responses)
                .build();
    }
}

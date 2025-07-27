package com.chungnam.eco.admin.controller.response;

import com.chungnam.eco.admin.service.dto.ChallengeDto;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeDetailResponse {
    private final Long challenge_id;
    private final String user_nickname;
    private final MissionInfo mission_info;
    private final List<String> image_url;
    private final String submissionText;
    private final String status;
    private final LocalDateTime created_at;

    @Builder
    @Getter
    private static class MissionInfo {
        private String title;
        private String type;
        private String level;
        private String category;
        private String description;
        private Integer rewardPoints;
    }

    public static ChallengeDetailResponse success(ChallengeDto challengeDto, List<String> image_url) {
        User user = challengeDto.getUser();
        Mission mission = challengeDto.getMission();

        MissionInfo missionInfo = MissionInfo.builder()
                .title(mission.getTitle())
                .category(mission.getCategory().getDescription())
                .type(mission.getType().name())
                .level(mission.getLevel().getDescription())
                .description(mission.getDescription())
                .rewardPoints(mission.getRewardPoints())
                .build();

        return ChallengeDetailResponse.builder()
                .challenge_id(challengeDto.getId())
                .user_nickname(user.getNickname())
                .mission_info(missionInfo)
                .created_at(challengeDto.getCreatedAt())
                .image_url(image_url)
                .submissionText(challengeDto.getSubmissionText())
                .status(challengeDto.getChallengeStatus().getDescription())
                .build();
    }
}

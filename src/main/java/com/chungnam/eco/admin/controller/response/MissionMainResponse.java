package com.chungnam.eco.admin.controller.response;

import com.chungnam.eco.mission.domain.MissionStatus;
import com.chungnam.eco.mission.service.dto.MissionDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionMainResponse {
    private final String title;
    private final String description;
    private final String type;
    private final MissionStatus status;
    private final String category;
    private final Integer rewardPoints;

    /**
     * 미션 생성 성공 시 반환할 응답값
     */
    public static MissionMainResponse success(MissionDto missionDto) {
        return MissionMainResponse.builder()
                .title(missionDto.getTitle())
                .description(missionDto.getDescription())
                .type(missionDto.getType())
                .status(missionDto.getStatus())
                .category(missionDto.getCategory())
                .rewardPoints(missionDto.getRewardPoints())
                .build();
    }
}

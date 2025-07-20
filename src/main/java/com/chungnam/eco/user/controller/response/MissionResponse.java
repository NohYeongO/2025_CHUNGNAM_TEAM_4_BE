package com.chungnam.eco.user.controller.response;

import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.service.dto.MissionDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String status;
    private String category;
    private int rewardPoints;

    public static MissionResponse from(MissionDto mission) {
        return MissionResponse.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .type(mission.getType())
                .status(mission.getStatus().name())
                .category(mission.getCategory())
                .rewardPoints(mission.getRewardPoints())
                .build();
    }
}

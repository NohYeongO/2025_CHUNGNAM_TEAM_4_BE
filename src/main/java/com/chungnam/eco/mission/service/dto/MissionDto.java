package com.chungnam.eco.mission.service.dto;

import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 미션 DTO (Mission 엔티티와 1:1 매핑)
 */
@Getter
@Builder
public class MissionDto {
    
    private final Long id;
    private final String title;
    private final String description;
    private final String type;
    private final MissionStatus status;
    private final String category;
    private final Integer rewardPoints;

    /**
     * Mission 엔티티에서 DTO로 변환
     */
    public static MissionDto from(Mission mission) {
        return MissionDto.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .type(mission.getType().name())
                .status(mission.getStatus())
                .category(mission.getCategory().getDescription())
                .rewardPoints(mission.getRewardPoints())
                .build();
    }
}

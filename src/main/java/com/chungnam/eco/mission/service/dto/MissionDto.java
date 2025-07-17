package com.chungnam.eco.mission.service.dto;

import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionCategory;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.domain.MissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 미션 DTO (Mission 엔티티와 동일한 구조)
 */
@Getter
@Builder
public class MissionDto {
    
    private final Long id;
    private final String title;
    private final String description;
    private final MissionType type;
    private final MissionStatus status;
    private final String category;
    private final Integer rewardPoints;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * Mission 엔티티에서 DTO로 변환
     */
    public static MissionDto from(Mission mission) {
        return MissionDto.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .type(mission.getType())
                .status(mission.getStatus())
                .category(mission.getCategory().getDescription())
                .rewardPoints(mission.getRewardPoints())
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }
}

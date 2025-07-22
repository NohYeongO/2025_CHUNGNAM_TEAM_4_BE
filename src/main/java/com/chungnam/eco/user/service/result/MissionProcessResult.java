package com.chungnam.eco.user.service.result;

import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import lombok.Builder;

import java.util.List;

@Builder
public record MissionProcessResult(
    boolean selected,
    List<UserMissionDto> existingMissions,
    List<MissionDto> randomMissions
) {
    
    public static MissionProcessResult selected(List<UserMissionDto> existingMissions) {
        return MissionProcessResult.builder()
                .selected(true)
                .existingMissions(existingMissions)
                .randomMissions(List.of())
                .build();
    }
    
    public static MissionProcessResult notSelected(List<MissionDto> randomMissions) {
        return MissionProcessResult.builder()
                .selected(false)
                .existingMissions(List.of())
                .randomMissions(randomMissions)
                .build();
    }
}

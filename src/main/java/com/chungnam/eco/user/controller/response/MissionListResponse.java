package com.chungnam.eco.user.controller.response;

import com.chungnam.eco.mission.service.dto.MissionDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MissionListResponse {
    
    private final boolean dailyMissionSelected; // 일일 미션 선택 완료 여부
    private final boolean weeklyMissionSelected; // 주간 미션 선택 완료 여부
    private final List<MissionDto> dailyMissions; // 일일 미션 5개
    private final List<MissionDto> weeklyMissions; // 주간 미션 3개
    
    public static MissionListResponse of(boolean dailySelected, boolean weeklySelected,
                                        List<MissionDto> dailyMissions, List<MissionDto> weeklyMissions) {
        return MissionListResponse.builder()
                .dailyMissionSelected(dailySelected)
                .weeklyMissionSelected(weeklySelected)
                .dailyMissions(dailyMissions)
                .weeklyMissions(weeklyMissions)
                .build();
    }
} 
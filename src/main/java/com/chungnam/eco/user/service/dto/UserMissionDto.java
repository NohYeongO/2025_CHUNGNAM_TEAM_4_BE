package com.chungnam.eco.user.service.dto;

import com.chungnam.eco.mission.service.dto.MissionDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 사용자 미션 정보 DTO
 * - 일일 미션과 주간 미션을 통합한 DTO
 */
@Getter
@Builder
public class UserMissionDto {

    private final List<MissionDto> dailyMissions;
    private final List<MissionDto> weeklyMissions;

    /**
     * 일일 미션과 주간 미션으로부터 DTO 생성
     *
     * @param dailyMissions  일일 미션 목록
     * @param weeklyMissions 주간 미션 목록
     * @return UserMissionDto
     */
    public static UserMissionDto of(List<MissionDto> dailyMissions, List<MissionDto> weeklyMissions) {
        return UserMissionDto.builder()
                .dailyMissions(dailyMissions)
                .weeklyMissions(weeklyMissions)
                .build();
    }
}

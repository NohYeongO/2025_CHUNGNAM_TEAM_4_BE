package com.chungnam.eco.mission.service.dto;

import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMissionDto {
    private Long userMissionId;
    private MissionDto missionDto;
    private String missionType; // "DAILY" 또는 "WEEKLY"
    private UserMissionStatus userMissionStatus;

    public static UserMissionDto from(UserMission userMission) {
        return UserMissionDto.builder()
                .userMissionId(userMission.getId())
                .missionDto(MissionDto.from(userMission.getMission()))
                .missionType(userMission.getMissionType().name())
                .userMissionStatus(userMission.getStatus())
                .build();
    }
}

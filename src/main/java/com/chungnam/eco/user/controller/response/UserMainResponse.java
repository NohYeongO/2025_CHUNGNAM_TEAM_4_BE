package com.chungnam.eco.user.controller.response;

import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 사용자 메인 페이지 응답 DTO
 * - 사용자 기본 정보 (이름, 포인트 등)
 * - 일일 미션 목록 (최대 3개)  
 * - 주간 미션 목록 (최대 2개)
 */
@Getter
@Builder
public class UserMainResponse {
    
    private final UserInfoDto userInfo;
    private final List<UserMissionDto> dailyMissions;
    private final List<UserMissionDto> weeklyMissions;

    public static UserMainResponse of(UserInfoDto userInfo, List<UserMissionDto> dailyMissions, List<UserMissionDto> weeklyMissions) {
        return UserMainResponse.builder()
                .userInfo(userInfo)
                .dailyMissions(dailyMissions)
                .weeklyMissions(weeklyMissions)
                .build();
    }
}

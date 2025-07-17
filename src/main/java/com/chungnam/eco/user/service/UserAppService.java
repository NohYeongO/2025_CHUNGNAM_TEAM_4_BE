package com.chungnam.eco.user.service;

import com.chungnam.eco.mission.service.UserFindMissionService;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserAuthService userAuthService;
    private final UserFindMissionService userFindMissionService;

    /**
     * 사용자 메인 정보 조회 서비스 (controller, service 중간계츨 - UserAppService)
     * @param userId 사용자 ID
     * @return 사용자 정보와 미션 목록을 포함한 응답 객체
     */
    public UserMainResponse getUserMainInfo(Long userId) {
        UserInfoDto userInfo = UserInfoDto.from(userAuthService.getUserById(userId));

        List<MissionDto> dailyMissions = userFindMissionService.getDailyMissions(userInfo);
        List<MissionDto> weeklyMissions = userFindMissionService.getWeeklyMissions(userInfo);

        return UserMainResponse.of(userInfo, dailyMissions, weeklyMissions);
    }
}

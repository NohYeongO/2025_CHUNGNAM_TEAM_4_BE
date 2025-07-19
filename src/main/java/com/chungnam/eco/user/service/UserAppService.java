package com.chungnam.eco.user.service;

import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.service.UserFindMissionService;
import com.chungnam.eco.mission.service.UserMissionSaveService;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserAuthService userAuthService;
    private final UserFindMissionService userFindMissionService;
    private final UserMissionSaveService userMissionSaveService;

    /**
     * 사용자 메인 정보 조회 서비스 (controller, service 중간계츨 - UserAppService)
     * @param userId 사용자 ID
     * @return 사용자 정보와 미션 목록을 포함한 응답 객체
     */
    public UserMainResponse getUserMainInfo(Long userId) {
        UserInfoDto userInfo = UserInfoDto.from(userAuthService.getUserById(userId));

        List<UserMissionDto> dailyMissions = getOrCreateMissions(userInfo, MissionType.DAILY, 3);
        List<UserMissionDto> weeklyMissions = getOrCreateMissions(userInfo, MissionType.WEEKLY, 2);

        return UserMainResponse.of(userInfo, dailyMissions, weeklyMissions);
    }

    private List<UserMissionDto> getOrCreateMissions(UserInfoDto userInfo, MissionType type, int limit) {
        List<UserMissionDto> missions = (type == MissionType.DAILY)
                ? userFindMissionService.getDailyMissions(userInfo)
                : userFindMissionService.getWeeklyMissions(userInfo);

        return missions.isEmpty()
                ? userMissionSaveService.findAndSaveNewMissions(userInfo, type, limit)
                : missions;
    }
}

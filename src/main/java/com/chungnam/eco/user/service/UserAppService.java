package com.chungnam.eco.user.service;

import com.chungnam.eco.mission.service.UserFindMissionService;
import com.chungnam.eco.mission.service.UserMissionSaveService;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
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

        List<UserMissionDto> dailyMissions = userFindMissionService.getDailyMissions(userInfo);
        List<UserMissionDto> weeklyMissions = userFindMissionService.getWeeklyMissions(userInfo);

        return UserMainResponse.of(userInfo, dailyMissions, weeklyMissions);
    }

    /**
     * 1. UserMission 테이블에서 일일/주간 미션 각각 조회
     * 2. 선택된 미션이 있으면 해당 미션들 반환, 없으면 Redis에서 랜덤 미션 조회
     * 3. 각각 boolean 값으로 선택 여부 구분
     * @param userId 사용자 ID
     * @return MissionListResponse
     */
    public MissionListResponse getMissionList(Long userId) {
        UserInfoDto userInfo = UserInfoDto.from(userAuthService.getUserById(userId));
        
        List<UserMissionDto> existingDailyMissions = userFindMissionService.getDailyMissions(userInfo);
        List<UserMissionDto> existingWeeklyMissions = userFindMissionService.getWeeklyMissions(userInfo);
        
        boolean dailySelected = !existingDailyMissions.isEmpty();
        List<MissionDto> dailyMissions;
        
        if (dailySelected) {
            dailyMissions = existingDailyMissions.stream()
                    .map(UserMissionDto::getMissionDto)
                    .toList();
        } else {
            dailyMissions = userFindMissionService.getRandomDailyMissions(userId);
        }
        
        boolean weeklySelected = !existingWeeklyMissions.isEmpty();
        List<MissionDto> weeklyMissions;
        
        if (weeklySelected) {
            weeklyMissions = existingWeeklyMissions.stream()
                    .map(UserMissionDto::getMissionDto)
                    .toList();
        } else {
            weeklyMissions = userFindMissionService.getRandomWeeklyMissions(userId);
        }
        
        return MissionListResponse.of(dailySelected, weeklySelected, dailyMissions, weeklyMissions);
    }

    public MissionResponse getMissionDetail(Long missionId) {
        return MissionResponse.from(userFindMissionService.getMissionDetail(missionId));
    }
}

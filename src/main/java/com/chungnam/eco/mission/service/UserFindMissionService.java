package com.chungnam.eco.mission.service;

import com.chungnam.eco.common.notification.DiscordNotificationService;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.domain.MissionStatus;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFindMissionService {

    private final MissionJPARepository missionRepository;
    private final UserMissionSaveAsyncService userMissionSaveAsyncService;
    private final DiscordNotificationService discordNotificationService;

    /**
     * 일일 미션 목록 조회 (Caffeine 캐시)
     */
    @Cacheable(value = "dailyMissions", key = "#userInfo.userId", cacheManager = "caffeineCacheManager")
    public List<MissionDto> getDailyMissions(UserInfoDto userInfo) {
        List<Mission> missions = missionRepository.findRandomActiveMissions(
                MissionType.DAILY.name(), MissionStatus.ACTIVATE.name(), 3);

        return saveUserMissionList(userInfo, missions, "일일미션");
    }

    /**
     * 주간 미션 목록 조회 (Redis 캐시)
     */
    @Cacheable(value = "weeklyMissions", key = "#userInfo.userId", cacheManager = "redisCacheManager")
    public List<MissionDto> getWeeklyMissions(UserInfoDto userInfo) {
        List<Mission> missions = missionRepository.findRandomActiveMissions(
                MissionType.WEEKLY.name(), MissionStatus.ACTIVATE.name(), 2);

        return saveUserMissionList(userInfo, missions, "주간미션");
    }

    /**
     * 사용자 미션 DB 목록 저장
     * @param userInfo 사용자 정보 DTO
     * @param missions DB에 저장할 미션 목록
     * @param missionType 미션 타입 (일일미션/주간미션)
     * @return 저장 완료한 MissionDto List
     */
    private List<MissionDto> saveUserMissionList(UserInfoDto userInfo, List<Mission> missions, String missionType) {
        List<MissionDto> missionDtos = missions.stream()
                .map(MissionDto::from)
                .toList();
                
        if (!missionDtos.isEmpty()) {
            userMissionSaveAsyncService.saveAllUserMissionsAsync(userInfo, missionDtos);
        } else {
            // 미션이 비어있으면 Discord 알림 전송
            log.warn("{} 미션이 비어있습니다 - 사용자: {}", missionType, userInfo.getUserId());
            discordNotificationService.sendMissionEmptyAlert(missionType, userInfo.getUserId());
        }
        
        return missionDtos;
    }
}

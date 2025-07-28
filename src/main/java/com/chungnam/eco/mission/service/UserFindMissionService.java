package com.chungnam.eco.mission.service;

import com.chungnam.eco.common.exception.InsufficientMissionException;
import com.chungnam.eco.common.exception.MissionNotFoundExcption;
import com.chungnam.eco.common.notification.DiscordNotificationService;
import com.chungnam.eco.mission.domain.*;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFindMissionService {

    private final UserMissionJPARepository userMissionRepository;
    private final MissionJPARepository missionRepository;
    private final DiscordNotificationService discordNotificationService;

    @Cacheable(value = "dailyMissions", key = "#userInfo.userId", cacheManager = "caffeineCacheManager",unless = "#result == null or #result.isEmpty()")
    public List<UserMissionDto> getDailyMissions(UserInfoDto userInfo) {
        return getUserMissions(userInfo, MissionType.DAILY);
    }

    @Cacheable(value = "weeklyMissions", key = "#userInfo.userId", cacheManager = "redisCacheManager",unless = "#result == null or #result.isEmpty()")
    public List<UserMissionDto> getWeeklyMissions(UserInfoDto userInfo) {
        return getUserMissions(userInfo, MissionType.WEEKLY);
    }

    @CacheEvict(value = "dailyMissions", key = "#userInfo.userId", cacheManager = "caffeineCacheManager")
    public void evictDailyMissionsCache(UserInfoDto userInfo) {
        // 캐시 무효화만 수행
    }

    @CacheEvict(value = "weeklyMissions", key = "#userInfo.userId", cacheManager = "redisCacheManager")
    public void evictWeeklyMissionsCache(UserInfoDto userInfo) {
        // 캐시 무효화만 수행
    }

    public void evictAllMissionsCache(UserInfoDto userInfo) {
        evictDailyMissionsCache(userInfo);
        evictWeeklyMissionsCache(userInfo);
    }

    private List<UserMissionDto> getUserMissions(UserInfoDto userInfo, MissionType missionType) {
        User user = userInfo.toEntity();

        List<UserMissionStatus> statuses = List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED);
        List<UserMission> userMissions = userMissionRepository.findByUserAndMissionTypeAndStatusIn(user, missionType, statuses);

        return userMissions.stream()
                .map(UserMissionDto::from)
                .toList();
    }

    @Cacheable(value = "randomDailyMissions", key = "#userId", cacheManager = "redisCacheManager")
    public List<MissionDto> getRandomDailyMissions(Long userId) {
        List<Mission> missions = missionRepository.findRandomActiveMissions(
                MissionType.DAILY.name(),
                MissionStatus.ACTIVATE.name(),
                5
        );

        if(missions.size() < 5){
            discordNotificationService.sendMissionEmptyAlert(MissionType.DAILY.name(), userId);
            throw new InsufficientMissionException("현재 일일 미션 목록이 부족합니다. 관리자에게 문의해주세요.");
        }

        return missions.stream()
                .map(MissionDto::from)
                .toList();
    }

    @Cacheable(value = "randomWeeklyMissions", key = "#userId", cacheManager = "redisCacheManager")
    public List<MissionDto> getRandomWeeklyMissions(Long userId) {
        List<Mission> missions = missionRepository.findRandomActiveMissions(
                MissionType.WEEKLY.name(),
                MissionStatus.ACTIVATE.name(),
                3
        );

        if(missions.size() < 3){
            discordNotificationService.sendMissionEmptyAlert(MissionType.WEEKLY.name(), userId);
            throw new InsufficientMissionException("현재 주간 미션 목록이 부족합니다. 관리자에게 문의해주세요.");
        }

        return missions.stream()
                .map(MissionDto::from)
                .toList();
    }

    public MissionDto getMissionDetail(Long missionId) {
        return missionRepository.findById(missionId)
                .map(MissionDto::from)
                .orElseThrow(MissionNotFoundExcption::new);
    }
}

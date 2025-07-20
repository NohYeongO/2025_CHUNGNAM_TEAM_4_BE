package com.chungnam.eco.mission.service;

import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFindMissionService {

    private final UserMissionJPARepository userMissionRepository;

    @Cacheable(value = "dailyMissions", key = "#userInfo.userId", cacheManager = "caffeineCacheManager")
    public List<UserMissionDto> getDailyMissions(UserInfoDto userInfo) {
        return getMissions(userInfo, MissionType.DAILY);
    }

    @Cacheable(value = "weeklyMissions", key = "#userInfo.userId", cacheManager = "redisCacheManager")
    public List<UserMissionDto> getWeeklyMissions(UserInfoDto userInfo) {
        return getMissions(userInfo, MissionType.WEEKLY);
    }

    private List<UserMissionDto> getMissions(UserInfoDto userInfo, MissionType missionType) {
        User user = userInfo.toEntity();

        List<UserMissionStatus> statuses = List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED);
        List<UserMission> userMissions = userMissionRepository.findByUserAndMissionTypeAndStatusIn(user, missionType, statuses);

        return userMissions.stream()
                .map(UserMissionDto::from)
                .toList();
    }
}

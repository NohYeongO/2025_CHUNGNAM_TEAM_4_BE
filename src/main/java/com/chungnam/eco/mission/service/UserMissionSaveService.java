package com.chungnam.eco.mission.service;

import com.chungnam.eco.common.notification.DiscordNotificationService;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionStatus;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMissionSaveService {

    private final UserMissionJPARepository userMissionRepository;
    private final MissionJPARepository missionRepository;
    private final DiscordNotificationService discordNotificationService;

    public List<UserMissionDto> findAndSaveNewMissions(UserInfoDto userInfo, MissionType missionType, int limit) {
        List<Mission> missions = missionRepository.findRandomActiveMissions(
                missionType.name(), MissionStatus.ACTIVATE.name(), limit);

        if (missions.isEmpty()) {
            discordNotificationService.sendMissionEmptyAlert(missionType.name(), userInfo.getUserId());
            return List.of();
        }

        List<UserMission> newUserMissions = missions.stream()
                .map(mission -> UserMission.builder()
                        .user(userInfo.toEntity())
                        .mission(mission)
                        .missionType(missionType)
                        .status(UserMissionStatus.IN_PROGRESS)
                        .build())
                .toList();

        userMissionRepository.saveAll(newUserMissions);

        return newUserMissions.stream()
                .map(UserMissionDto::from)
                .toList();
    }
}

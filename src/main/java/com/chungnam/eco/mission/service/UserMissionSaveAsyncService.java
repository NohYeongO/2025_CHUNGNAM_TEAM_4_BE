package com.chungnam.eco.mission.service;

import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.service.dto.MissionDto;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.UserMission;
import com.chungnam.eco.user.repository.UserMissionJPARepository;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMissionSaveAsyncService {

    private final UserMissionJPARepository userMissionRepository;
    private final MissionJPARepository missionRepository;

    /**
     * 모든 미션을 한번에 비동기로 저장
     */
    @Async("taskExecutor")
    public void saveAllUserMissionsAsync(UserInfoDto userInfo, List<MissionDto> missionDtos) {
        try {
            User user = userInfo.toEntity();
            LocalDateTime assignedDate = LocalDateTime.now().with(LocalTime.MIN);
            
            // MissionDto에서 ID를 추출하고 실제 Mission 엔티티들 조회
            List<Mission> missions = missionRepository.findAllById(
                    missionDtos.stream().map(MissionDto::getId).toList()
            );
            
            // UserMission 엔티티 생성
            List<UserMission> userMissions = missions.stream()
                    .map(mission -> UserMission.builder()
                            .user(user)
                            .mission(mission)
                            .assignedDate(assignedDate)
                            .build())
                    .toList();

            userMissionRepository.saveAll(userMissions);
        } catch (Exception e) {
            log.error("[비동기] 전체 미션 저장 실패 - 사용자: {}", userInfo.getUserId(), e);
        }
    }
}

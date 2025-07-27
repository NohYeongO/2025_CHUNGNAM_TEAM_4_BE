package com.chungnam.eco.mission.service;

import com.chungnam.eco.common.exception.DataIntegrityException;
import com.chungnam.eco.common.exception.MissionNotFoundExcption;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMissionSaveService {

    private final UserMissionJPARepository userMissionRepository;
    private final MissionJPARepository missionRepository;

    public void submitMissionStatusUpdate(Long userMissionId) {
        UserMission userMission = userMissionRepository.findById(userMissionId).orElseThrow(() -> {
            log.error("UserMission not found with ID: {}", userMissionId);
            return new DataIntegrityException();
        });
        userMission.submitStatusUpdate();
        userMissionRepository.save(userMission);
    }

    /**
     * 사용자의 미션 선택을 저장합니다
     * @param user 사용자
     * @param missionIds 선택된 미션 ID 리스트
     * @param missionType 미션 타입 (DAILY/WEEKLY)
     * @return 저장된 UserMission 개수
     */
    @Transactional
    public int saveSelectedMissions(UserInfoDto user, List<Long> missionIds, MissionType missionType) {
        List<Mission> missions = missionRepository.findAllById(missionIds);
        
        if (missions.size() != missionIds.size()) {
            throw new MissionNotFoundExcption("일부 미션을 찾을 수 없습니다.");
        }

        List<UserMission> userMissions = missions.stream()
                .map(mission -> UserMission.builder()
                        .user(user.toEntity())
                        .mission(mission)
                        .missionType(missionType)
                        .status(UserMissionStatus.IN_PROGRESS)
                        .build())
                .toList();
        
        userMissionRepository.saveAll(userMissions);

        return userMissions.size();
    }
}

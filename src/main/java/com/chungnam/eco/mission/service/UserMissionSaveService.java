package com.chungnam.eco.mission.service;

import com.chungnam.eco.common.exception.DataIntegrityException;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMissionSaveService {

    private final UserMissionJPARepository missionRepository;

    public void submitMissionStatusUpdate(Long userMissionId) {
        UserMission userMission = missionRepository.findById(userMissionId).orElseThrow(() -> {
            log.error("UserMission not found with ID: {}", userMissionId);
            return new DataIntegrityException();
        });
        userMission.submitStatusUpdate();
    }
}

package com.chungnam.eco.mission.service;

import com.chungnam.eco.admin.controller.request.CreateMissionRequest;
import com.chungnam.eco.admin.controller.request.EditMissionRequest;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.mission.domain.MissionStatus;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.mission.service.dto.MissionDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionJPARepository missionJPARepository;

    /**
     * 미션 ID(PK)로 미션을 조회합니다.
     *
     * @param missionId 조회할 미션의 ID
     * @return 조회된 {@link Mission} 엔티티
     */
    @Transactional(readOnly = true)
    public Mission findMissionById(Long missionId) {
        return missionJPARepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 mission이 존재하지 않습니다. : " + missionId));
    }

    /**
     * 새로운 미션을 생성합니다.
     *
     * @param request 생성할 미션의 요청 DTO
     * @return 생성된 미션의 정보가 담긴 {@link MissionDto}
     */
    @Transactional
    public MissionDto creatMission(CreateMissionRequest request) {
        Mission mission = Mission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .category(request.getCategory())
                .level(request.getLevel())
                .rewardPoints(request.getRewardPoints())
                .build();

        Mission savedMission = missionJPARepository.save(mission);

        return MissionDto.from(savedMission);
    }

    /**
     * 특정 미션을 활성화 상태로 변경합니다.
     *
     * @param missionIdList 활성화할 미션의 ID
     * @return 활성화된 미션의 정보가 담긴 {@link MissionDto}
     */
    @Transactional
    public List<MissionDto> activateMission(List<Long> missionIdList) {
        List<MissionDto> missionDtoList = new ArrayList<>();
        for (Long id : missionIdList) {
            Mission mission = findMissionById(id);
            mission.activate(); // 미션 활성화
            missionDtoList.add(MissionDto.from(mission));
        }
        return missionDtoList;
    }

    /**
     * 특정 미션을 비활성화(삭제) 상태로 변경합니다.
     *
     * @param missionId 비활성화할 미션의 ID
     * @return 비활성화된 미션의 정보가 담긴 {@link MissionDto}
     */
    @Transactional
    public List<MissionDto> deactivateMission(List<Long> missionId) {
        List<MissionDto> missionDtoList = new ArrayList<>();
        for (Long id : missionId) {
            Mission mission = findMissionById(id);
            mission.disable(); // 미션 비활성화
            missionDtoList.add(MissionDto.from(mission));
        }
        return missionDtoList;
    }

    /**
     * @param status 상태값 문자열
     * @return 변환된 {@link MissionStatus} enum
     */
    public MissionStatus toMissionStatus(String status) {
        try {
            return MissionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Mission의 잘못된 상태값입니다. : " + status);
        }
    }

    /**
     * 특정 상태를 기준으로 미션 목록을 조회합니다.
     *
     * @param status 조회할 상태값 문자열, null 이면 전체 조회
     * @return 조회된 미션 목록의 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MissionDto> findMissionList(String status) {

        if (status == null || status.isBlank()) {
            // status가 null 또는 공백이면 전체 조회
            return missionJPARepository.findAll().stream()
                    .map(MissionDto::from)
                    .toList();
        }
        // status가 있으면 MissionStatus로 변환 후 조건 조회
        MissionStatus missionStatus = toMissionStatus(status);
        List<Mission> missionList = missionJPARepository.findByStatus(missionStatus);

        return missionList.stream()
                .map(MissionDto::from)
                .toList();
    }

    /**
     * 미션 수정
     *
     * @param missionId          수정할 미션 ID
     * @param editMissionRequest 수정할 내용을 담은 request
     * @return 수정후 MissionDTO 변환
     */
    @Transactional
    public MissionDto editMission(Long missionId, EditMissionRequest editMissionRequest) {
        Mission mission = findMissionById(missionId);
        mission.edit(
                editMissionRequest.getTitle(),
                editMissionRequest.getType(),
                editMissionRequest.getDescription(),
                editMissionRequest.getLevel(),
                editMissionRequest.getCategory(),
                editMissionRequest.getRewardPoints()
        );

        return MissionDto.from(mission);
    }
}

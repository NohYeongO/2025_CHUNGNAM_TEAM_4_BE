package com.chungnam.eco.admin.controller;

import com.chungnam.eco.admin.controller.request.CreateMissionRequest;
import com.chungnam.eco.admin.controller.request.EditMissionRequest;
import com.chungnam.eco.admin.controller.response.AllMissionResponse;
import com.chungnam.eco.admin.controller.response.MissionMainResponse;
import com.chungnam.eco.mission.service.MissionService;
import com.chungnam.eco.mission.service.dto.MissionDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMissionController {
    private final MissionService missionService;

    /**
     * 미션 생성 API
     *
     * @param createMissionRequest 미션 생성 요청 DTO (title, description, type, category, rewardPoints)
     * @return ResponseEntity(MissionMainResponse) 생성된 미션 정보
     */
    @PostMapping("/missions")
    public ResponseEntity<?> createMission(@RequestBody CreateMissionRequest createMissionRequest) {

        MissionDto missionDto = missionService.creatMission(createMissionRequest); // MissionDto 생성
        MissionMainResponse response = MissionMainResponse.success(missionDto);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 미션을 활성화 상태로 변경합니다.
     *
     * @param missionId 활성화할 미션의 ID
     * @return ResponseEntity(MissionMainResponse) 활성화된 미션 정보
     */

    @PatchMapping("/missions/{missionId}/activate")
    public ResponseEntity<?> activateMission(@PathVariable Long missionId) {

        MissionDto missionDto = missionService.activateMission(missionId); // Mission status activate로 변경
        MissionMainResponse response = MissionMainResponse.success(missionDto);

        return ResponseEntity.ok(response);
    }


    /**
     * 특정 미션 목록을 조회합니다.
     *
     * @param status (optional) 조회할 미션 상태 (CREATE, ACTIVATE, DELETE) null 이면 전체 조회
     * @return ResponseEntity(AllMissionResponse) 미션 목록
     */
    @GetMapping("/missions")
    public ResponseEntity<?> getAllMissions(@RequestParam(required = false) String status) {

        List<MissionDto> missionDtoList = missionService.findMissionList(status); // 상태값으로 미션 조회
        AllMissionResponse response = AllMissionResponse.success(missionDtoList);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 미션을 삭제(비활성화)합니다.
     *
     * @param missionId 삭제할 미션의 ID
     * @return ResponseEntity(MissionMainResponse) 삭제된 미션 정보
     */
    @PatchMapping("/missions/{missionId}/delete")
    public ResponseEntity<?> deleteMission(@PathVariable Long missionId) {

        MissionDto missionDto = missionService.deactivateMission(missionId); // Mission status delete로 변경
        MissionMainResponse response = MissionMainResponse.success(missionDto);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 미션을 수정합니다.
     *
     * @param missionId          수정할 미션의 ID
     * @param editMissionRequest 수정할 내용이 담긴 요청 DTO
     * @return ResponseEntity(MissionMainResponse) 수정된 미션 정보
     */
    @PatchMapping("/missions/{missionId}")
    public ResponseEntity<?> editMission(@PathVariable Long missionId,
                                         @RequestBody EditMissionRequest editMissionRequest) {
        MissionDto missionDto = missionService.editMission(missionId, editMissionRequest); // 미션 수정
        MissionMainResponse response = MissionMainResponse.success(missionDto);
        return ResponseEntity.ok(response);

    }
}

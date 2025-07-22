package com.chungnam.eco.user.service;

import com.chungnam.eco.challenge.service.ChallengeSubmitService;
import com.chungnam.eco.challenge.service.dto.ChallengeDto;
import com.chungnam.eco.common.exception.DataIntegrityException;
import com.chungnam.eco.common.exception.ImageUploadException;
import com.chungnam.eco.common.exception.InvalidMissionStatusException;
import com.chungnam.eco.common.storage.AzureBlobStorageService;
import com.chungnam.eco.common.storage.ImageUploadDto;
import com.chungnam.eco.mission.service.UserFindMissionService;
import com.chungnam.eco.mission.service.UserMissionSaveService;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
import com.chungnam.eco.user.controller.response.MissionSubmitResponse;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.service.dto.*;
import com.chungnam.eco.user.service.result.MissionProcessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserAuthService userAuthService;
    private final UserFindMissionService userFindMissionService;
    private final AzureBlobStorageService azureBlobStorageService;
    private final ChallengeSubmitService challengeSubmitService;
    private final UserMissionSaveService userMissionSaveService;

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
        boolean dailySelected = !existingDailyMissions.isEmpty();

        MissionProcessResult dailyResult = dailySelected
                ? MissionProcessResult.selected(existingDailyMissions)
                : MissionProcessResult.notSelected(userFindMissionService.getRandomDailyMissions(userId));

        List<UserMissionDto> existingWeeklyMissions = userFindMissionService.getWeeklyMissions(userInfo);
        boolean weeklySelected = !existingWeeklyMissions.isEmpty();

        MissionProcessResult weeklyResult = weeklySelected
                ? MissionProcessResult.selected(existingWeeklyMissions)
                : MissionProcessResult.notSelected(userFindMissionService.getRandomWeeklyMissions(userId));

        return MissionListResponse.of(
            dailyResult.selected(), 
            weeklyResult.selected(),
            dailyResult.existingMissions(), 
            weeklyResult.existingMissions(), 
            dailyResult.randomMissions(), 
            weeklyResult.randomMissions()
        );
    }

    public MissionResponse getMissionDetail(Long missionId) {
        return MissionResponse.from(userFindMissionService.getMissionDetail(missionId));
    }

    public MissionSubmitResponse submitMission(Long userId, Long userMissionId, String description, List<MultipartFile> images) {
        ChallengeDto tempChallenge = null;
        try {
            UserInfoDto userInfo = UserInfoDto.from(userAuthService.getUserById(userId));
            tempChallenge = challengeSubmitService.createTempChallenge(userInfo, userMissionId);
            List<ImageUploadDto> ImageUploadDto = azureBlobStorageService.uploadImages(images);
            Long challengeId = challengeSubmitService.completeMissionSubmit(tempChallenge, ImageUploadDto, description);
            userMissionSaveService.submitMissionStatusUpdate(userMissionId);
            return MissionSubmitResponse.success(challengeId);
        } catch (InvalidMissionStatusException e){
           throw new InvalidMissionStatusException();
        } catch (ImageUploadException | DataIntegrityException e){
            if (tempChallenge != null){
                challengeSubmitService.deleteTempChallenge(tempChallenge);
            }
            throw new ImageUploadException(e.getMessage());
        }
    }
}

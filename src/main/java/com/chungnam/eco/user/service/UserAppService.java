package com.chungnam.eco.user.service;

import com.chungnam.eco.challenge.service.ChallengeSubmitService;
import com.chungnam.eco.challenge.service.dto.ChallengeDto;
import com.chungnam.eco.common.exception.DataIntegrityException;
import com.chungnam.eco.common.exception.ImageUploadException;
import com.chungnam.eco.common.exception.InvalidMissionStatusException;
import com.chungnam.eco.common.exception.MissionChoiceException;
import com.chungnam.eco.common.exception.MissionNotFoundExcption;
import com.chungnam.eco.common.exception.InsufficientMissionException;
import com.chungnam.eco.common.storage.AzureBlobStorageService;
import com.chungnam.eco.common.storage.ImageUploadDto;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.service.UserFindMissionService;
import com.chungnam.eco.mission.service.UserMissionSaveService;
import com.chungnam.eco.mission.service.dto.UserMissionDto;
import com.chungnam.eco.user.controller.request.MissionChoiceRequest;
import com.chungnam.eco.user.controller.response.MissionChoiceResponse;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
import com.chungnam.eco.user.controller.response.MissionSubmitResponse;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.service.dto.*;
import com.chungnam.eco.user.service.result.MissionProcessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 미션 선택 처리
     * @param userId 사용자 ID
     * @param request 선택된 미션 ID 목록 (일일 3개, 주간 1개)
     * @return 미션 선택 결과
     */
    @Transactional
    public MissionChoiceResponse chooseMissions(Long userId, MissionChoiceRequest request) {
        try {
            UserInfoDto userInfo = UserInfoDto.from(userAuthService.getUserById(userId));

            int dailyCount = userMissionSaveService.saveSelectedMissions(
                    userInfo, request.getDailyMissionIds(), MissionType.DAILY);
            int weeklyCount = userMissionSaveService.saveSelectedMissions(
                    userInfo, request.getWeeklyMissionIds(), MissionType.WEEKLY);
            
            log.info("사용자 {}의 미션 선택 완료 - 일일: {}개, 주간: {}개", 
                    userId, dailyCount, weeklyCount);
            
            return MissionChoiceResponse.success(dailyCount, weeklyCount);
        } catch (MissionNotFoundExcption | InsufficientMissionException e) {
            throw e;
        } catch (Exception e) {
            log.error("미션 선택 중 오류 발생 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            throw new MissionChoiceException("미션 선택 중 오류가 발생했습니다: " + e.getMessage());
        }
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

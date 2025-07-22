package com.chungnam.eco.challenge.service;

import com.chungnam.eco.challenge.domain.Challenge;
import com.chungnam.eco.challenge.repository.ChallengeJPARepository;
import com.chungnam.eco.challenge.repository.ChallengeImageJPARepository;
import com.chungnam.eco.challenge.service.dto.ChallengeDto;
import com.chungnam.eco.common.exception.DataIntegrityException;
import com.chungnam.eco.common.exception.InvalidMissionStatusException;
import com.chungnam.eco.common.exception.MissionNotFoundExcption;
import com.chungnam.eco.common.storage.ImageUploadDto;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
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
public class ChallengeSubmitService {

    private final ChallengeJPARepository challengeRepository;
    private final ChallengeImageJPARepository challengeImageRepository;
    private final UserMissionJPARepository userMissionRepository;

    @Transactional
    public ChallengeDto createTempChallenge(UserInfoDto userInfo, Long userMissionId) {

        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(MissionNotFoundExcption::new);

        if (userMission.getStatus() != UserMissionStatus.IN_PROGRESS) {
            throw new InvalidMissionStatusException();
        }

        Challenge tempChallenge = Challenge.builder()
                .user(userInfo.toEntity())
                .mission(userMission.getMission())
                .build();

        return ChallengeDto.from(challengeRepository.save(tempChallenge), userInfo.getUserId(), userMissionId);
    }

    @Transactional
    public Long completeMissionSubmit(ChallengeDto tempChallenge, List<ImageUploadDto> uploadImageList, String submissionText) {
        Challenge challenge = challengeRepository.findById(tempChallenge.getChallengeId()).orElseThrow(() -> {
            log.error("Challenge not found with ID: {}", tempChallenge.getChallengeId());
            return new DataIntegrityException();
        });
        challengeImageRepository.saveAll(
                uploadImageList.stream()
                        .map(uploadImage -> ImageUploadDto.toEntity(challenge, uploadImage))
                        .toList()
        );
        challenge.submitCompleted(submissionText);
        return challenge.getId();
    }

    public void deleteTempChallenge(ChallengeDto tempChallenge) {
        challengeRepository.deleteById(tempChallenge.getChallengeId());
    }
}

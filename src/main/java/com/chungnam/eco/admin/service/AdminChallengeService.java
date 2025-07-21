package com.chungnam.eco.admin.service;

import com.chungnam.eco.admin.service.dto.ChallengeDto;
import com.chungnam.eco.challenge.domain.Challenge;
import com.chungnam.eco.challenge.domain.ChallengeImage;
import com.chungnam.eco.challenge.domain.ChallengeStatus;
import com.chungnam.eco.challenge.repository.ChallengeImageJPARepository;
import com.chungnam.eco.challenge.repository.ChallengeJPARepository;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminChallengeService {

    private final ChallengeJPARepository challengeJPARepository;
    private final ChallengeImageJPARepository challengeImageJPARepository;

    /**
     * 챌린지를 ID로 조회합니다.
     *
     * @param id 조회할 챌린지 ID
     * @return Challenge 엔티티
     * @throws IllegalArgumentException 유효하지 않은 ID인 경우
     */
    @Transactional(readOnly = true)
    public Challenge findChallenge(Long id) {
        return challengeJPARepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Challenge의 잘못된 id값입니다. : " + id));
    }

    /**
     * 챌린지의 이미지 목록을 조회합니다. (sort 순서 보장)
     *
     * @param id 챌린지 ID
     * @return ChallengeImage 리스트
     */
    @Transactional(readOnly = true)
    public List<ChallengeImage> findChallengeImage(Long id) {
        return challengeImageJPARepository.findByChallengeIdOrderBySort(id);
    }

    /**
     * 문자열로 전달된 상태값을 ChallengeStatus enum으로 변환합니다.
     *
     * @param status 상태값 (예: PENDING, REJECTED, IN_PROGRESS, COMPLETED)
     * @return ChallengeStatus enum
     * @throws IllegalArgumentException 잘못된 상태값인 경우
     */
    public ChallengeStatus toChallengeStatus(String status) {
        try {
            return ChallengeStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Challenge의 잘못된 상태값입니다. : " + status);
        }
    }

    /**
     * 상태값과 페이징 조건에 맞는 챌린지 목록을 조회합니다. 상태값이 null 또는 비어있으면 전체 조회합니다.
     *
     * @param status   필터링할 상태값
     * @param pageable 페이징 정보
     */
    @Transactional(readOnly = true)
    public List<ChallengeDto> getChallengeList(String status, Pageable pageable) {
        if (status == null || status.isEmpty()) {
            return challengeJPARepository.findAll(pageable)
                    .map(ChallengeDto::from)
                    .stream()
                    .toList();
        }
        ChallengeStatus challengeStatus = toChallengeStatus(status);
        Page<Challenge> challengePage = challengeJPARepository.findByChallengeStatus(challengeStatus, pageable);
        return challengePage.map(ChallengeDto::from)
                .stream()
                .toList();
    }

    /**
     * 특정 챌린지를 조회하여 DTO로 반환합니다.
     *
     * @param challengeId 챌린지 ID
     */
    @Transactional(readOnly = true)
    public ChallengeDto getChallenge(Long challengeId) {
        Challenge challenge = findChallenge(challengeId);
        return ChallengeDto.from(challenge);
    }

    /**
     * 특정 챌린지의 이미지 URL 목록을 조회합니다.
     *
     * @param challengeId 챌린지 ID
     */
    @Transactional(readOnly = true)
    public List<String> getChallengeImages(Long challengeId) {
        List<ChallengeImage> challengeImageList = findChallengeImage(challengeId);
        return challengeImageList.stream()
                .map(ChallengeImage::getUrl)
                .toList();
    }

    /**
     * 특정 챌린지를 승인 처리합니다. 챌린지의 상태를 COMPLETED로 변경하고, 유저에게 포인트를 지급합니다.
     *
     * @param challengeId 승인할 챌린지 ID
     */
    @Transactional
    public void approveChallenge(Long challengeId) {
        Challenge challenge = findChallenge(challengeId);

        User user = challenge.getUser();
        Mission mission = challenge.getMission();
        Integer point = mission.getRewardPoints();
        user.SupplyPoint(point);

        challenge.setCompleted();
    }

    /**
     * 특정 챌린지를 거절 처리합니다. 기존 submissionText에 거절 사유를 저장합니다..
     *
     * @param challengeId 거절할 챌린지 ID
     * @param reason      거절 사유
     */
    @Transactional
    public void rejectChallenge(Long challengeId, String reason) {
        Challenge challenge = findChallenge(challengeId);

        challenge.setRejected("거절 사유: " + reason);
    }
}

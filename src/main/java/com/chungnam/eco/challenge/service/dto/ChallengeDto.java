package com.chungnam.eco.challenge.service.dto;

import com.chungnam.eco.challenge.domain.Challenge;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeDto {
    private Long challengeId;
    private Long userId;
    private Long missionId;
    private String submissionText;
    private String status;

    public static ChallengeDto from(Challenge challenge, Long userId, Long missionId) {
        return ChallengeDto.builder()
                .challengeId(challenge.getId())
                .userId(userId)
                .missionId(missionId)
                .submissionText(challenge.getSubmissionText())
                .status(challenge.getChallengeStatus().name())
                .build();
    }
}

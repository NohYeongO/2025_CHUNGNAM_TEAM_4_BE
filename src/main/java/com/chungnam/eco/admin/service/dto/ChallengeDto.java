package com.chungnam.eco.admin.service.dto;

import com.chungnam.eco.challenge.domain.Challenge;
import com.chungnam.eco.challenge.domain.ChallengeStatus;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.user.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeDto {
    private Long id;
    private User user;
    private Mission mission;
    private String submissionText;
    private ChallengeStatus challengeStatus;
    private LocalDateTime createdAt;


    /**
     * entity를 dto로 변환합니다.
     */
    public static ChallengeDto from(Challenge challenge) {
        return ChallengeDto.builder()
                .id(challenge.getId())
                .user(challenge.getUser())
                .mission(challenge.getMission())
                .submissionText(challenge.getSubmissionText())
                .challengeStatus(challenge.getChallengeStatus())
                .createdAt(challenge.getCreatedAt())
                .build();
    }


}

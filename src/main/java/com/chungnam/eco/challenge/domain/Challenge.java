package com.chungnam.eco.challenge.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "challenge")
public class Challenge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Lob
    @Column(name = "submission_text")
    private String submissionText;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Challenge(User user, Mission mission) {
        this.user = user;
        this.mission = mission;
        this.startedAt = LocalDateTime.now();
        this.challengeStatus = ChallengeStatus.IN_PROGRESS; // 미션 진행
    }

    /**
     * 챌린지 승인 요청 상태로 전환합니다.
     *
     */
    public void submitCompleted(String submissionText) {
        this.challengeStatus = ChallengeStatus.PENDING; // 미션 승인 요청
        this.submissionText = submissionText; // 미션 완료 후 설명글 추가
    }

    /**
     * 챌린지 승인을 거절합니다.
     *
     * @param rejectionReason 거절 사유
     */
    public void setRejected(String rejectionReason) {
        this.challengeStatus = ChallengeStatus.REJECTED; // 미션 승인 거절
        this.submissionText = rejectionReason; // 거절 사유 추가
    }

    /**
     * 지정한 시각에 챌린지를 완료 상태로 전환합니다.
     */
    public void setCompleted() {
        this.completedAt = LocalDateTime.now();
        this.challengeStatus = ChallengeStatus.COMPLETED; // 미션 완료
    }
}

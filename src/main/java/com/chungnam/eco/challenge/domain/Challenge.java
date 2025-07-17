package com.chungnam.eco.challenge.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Lob
    @Column(name = "submission_text")
    private String submissionText;

    @Column(name = "status")
    private ChallengeStatus challengeStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name="completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Challenge(User user, Mission mission, String submissionText) {
        this.user = user;
        this.mission = mission;
        this.submissionText = submissionText;
        this.challengeStatus = ChallengeStatus.BEFORE; // 미션 진행전으로 초기화
    }

    /**
     * 지정한 시각에 챌린지를 시작 상태로 전환합니다.
     * @param startedAt 시작 시각
     */
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
        this.challengeStatus = ChallengeStatus.IN_PROGRESS; // 미션 진행
    }


    /**
     * 지정한 시각에 챌린지를 완료 상태로 전환합니다.
     * @param completedAt 시작 시각
     */
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        this.challengeStatus = ChallengeStatus.COMPLETED; // 미션 완료
    }
}

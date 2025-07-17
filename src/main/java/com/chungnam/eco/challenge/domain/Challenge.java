package com.chungnam.eco.challenge.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.mission.domain.Mission;
import com.chungnam.eco.user.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Lob
    @Column(name = "submission_text")
    private String submissionText;

    @Column(name = "status")
    private ChallengeStatus challengeStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Challenge(Member member, Mission mission) {
        this.member = member;
        this.mission = mission;
        this.startedAt = LocalDateTime.now();
        this.challengeStatus = ChallengeStatus.IN_PROGRESS; // 미션 진행
    }

    /**
     * 챌린지 승인 요청 상태로 전환합니다.
     *
     * @param description 승인 요청 시 제출한 설명
     */
    public void setPending(String description) {
        this.challengeStatus = ChallengeStatus.PENDING; // 미션 승인 요청
        this.submissionText = description; // 미션 완료 후 설명글 추가
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

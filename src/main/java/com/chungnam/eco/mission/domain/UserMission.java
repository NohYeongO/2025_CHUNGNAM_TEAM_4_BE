package com.chungnam.eco.mission.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자별 오늘의 미션 저장 테이블
 * - 캐시와 함께 DB에도 저장하여 정합성 보장
 * - 미션 참여 상태 추적
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_mission")
public class UserMission extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name= "mission_type", nullable = false, length = 20)
    private MissionType missionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserMissionStatus status = UserMissionStatus.IN_PROGRESS;

    @Builder
    public UserMission(User user, Mission mission, MissionType missionType, UserMissionStatus status) {
        this.user = user;
        this.mission = mission;
        this.missionType = missionType;
        this.status = status != null ? status : UserMissionStatus.IN_PROGRESS; // 기본 상태는 IN_PROGRESS
    }

    public void submitStatusUpdate() {
        this.status = UserMissionStatus.SUBMITTED;
    }
}

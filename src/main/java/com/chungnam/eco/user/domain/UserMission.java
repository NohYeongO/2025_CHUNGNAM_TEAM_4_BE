package com.chungnam.eco.user.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.mission.domain.Mission;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자별 오늘의 미션 저장 테이블
 * - 캐시와 함께 DB에도 저장하여 정합성 보장
 * - 미션 참여 상태 추적
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_mission")
public class UserMission extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserMissionStatus status = UserMissionStatus.NOT_STARTED;
    
    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "points_earned")
    private Integer pointsEarned;
}

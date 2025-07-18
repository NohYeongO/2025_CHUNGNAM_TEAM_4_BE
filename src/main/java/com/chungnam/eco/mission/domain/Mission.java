package com.chungnam.eco.mission.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mission")
public class Mission extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private MissionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private MissionCategory category;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints;

    @Builder
    public Mission(String title, String description, MissionType type, MissionCategory category, Integer rewardPoints) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.status = MissionStatus.CREATE; // 생성으로 초기화
        this.rewardPoints = rewardPoints;
    }

    /**
     * 미션을 활성화 상태로 변경합니다.
     */
    public void activate() {
        this.status = MissionStatus.ACTIVATE;
    }

    /**
     * 미션을 삭제 상태로 변경합니다.
     */
    public void delete() {
        this.status = MissionStatus.DELETE;
    }
}

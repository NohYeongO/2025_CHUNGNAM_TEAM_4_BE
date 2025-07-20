package com.chungnam.eco.mission.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(name = "level", nullable = false, length = 20)
    private MissionLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private MissionCategory category;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints;

    @Builder
    public Mission(String title, String description, MissionType type, MissionLevel level, MissionCategory category,
                   Integer rewardPoints) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.level = level;
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

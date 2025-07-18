package com.chungnam.eco.mission.domain;

import lombok.Getter;

/**
 * 미션 장르 구분
 */
@Getter
public enum MissionCategory {
    
    DAILY_HABIT("일상 속 습관"),
    ECO_TRANSPORTATION("친환경 이동"),
    ECO_CONSUMPTION("친환경 소비"),
    RECYCLING("재활용/자원순환"),
    ENERGY_SAVING("에너지 절약"),
    LOW_CARBON_DIET("저탄소 식생활"),
    ENVIRONMENTAL_EDUCATION("환경 교육/확산"),
    COMMUNITY_ACTIVITY("지역사회/공동체 활동");

    private final String description;

    MissionCategory(String description) {
        this.description = description;
    }
}

package com.chungnam.eco.mission.domain;

import lombok.Getter;

@Getter
public enum MissionLevel {
    HIGH("상"),
    MIDDLE("중"),
    LOW("하");

    private final String description;

    MissionLevel(String description) {
        this.description = description;
    }
}

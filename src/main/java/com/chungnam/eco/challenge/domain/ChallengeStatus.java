package com.chungnam.eco.challenge.domain;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    PENDING("요청"),
    REJECTED("거절"),
    IN_PROGRESS("진행중"),
    COMPLETED("승인");

    private final String description;

    ChallengeStatus(String description) {
        this.description = description;
    }
}

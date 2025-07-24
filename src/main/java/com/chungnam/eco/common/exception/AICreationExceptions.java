package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class AICreationExceptions extends RuntimeException {
    private final ErrorCode errorCode;

    public AICreationExceptions(String message) {
        super(message);
        this.errorCode = ErrorCode.AI_MISSION_CREATION_FAILED;
    }
}

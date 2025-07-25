package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class MissionChoiceException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public MissionChoiceException(String message) {
        super(message);
        this.errorCode = ErrorCode.MISSION_CHOICE_ERROR;
    }
} 
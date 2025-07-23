package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class InsufficientMissionException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public InsufficientMissionException(String message) {
        super(message);
        this.errorCode = ErrorCode.INSUFFICIENT_MISSION;
    }
} 
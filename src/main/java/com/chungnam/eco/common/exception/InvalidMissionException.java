package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class InvalidMissionException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidMissionException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_MISSION;
    }
}

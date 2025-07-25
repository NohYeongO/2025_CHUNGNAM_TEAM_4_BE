package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class InvalidMissionStatusException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidMissionStatusException() {
        this.errorCode = ErrorCode.INVALID_MISSION_STATUS;
    }

    public InvalidMissionStatusException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_MISSION_STATUS;
    }
}

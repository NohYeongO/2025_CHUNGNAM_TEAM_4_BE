package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class InvalidChallengeException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidChallengeException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_CHALLENGE;
    }
}

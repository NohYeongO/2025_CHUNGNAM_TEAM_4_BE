package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class DataIntegrityException extends RuntimeException {
    private final ErrorCode errorCode;

    public DataIntegrityException() {
        this.errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
    }

    public DataIntegrityException(String message) {
        super(message);
        this.errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
    }
}

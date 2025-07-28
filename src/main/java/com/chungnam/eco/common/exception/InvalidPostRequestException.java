package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class InvalidPostRequestException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public InvalidPostRequestException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_POST_REQUEST;
    }
    
    public InvalidPostRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public InvalidPostRequestException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

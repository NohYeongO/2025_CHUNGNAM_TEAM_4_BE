package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public PostNotFoundException(String message) {
        super(message);
        this.errorCode = ErrorCode.POST_NOT_FOUND;
    }

    public PostNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

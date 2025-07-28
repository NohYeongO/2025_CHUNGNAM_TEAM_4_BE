package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class PostCreateException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public PostCreateException(String message) {
        super(message);
        this.errorCode = ErrorCode.POST_CREATE_FAILED;
    }
    
    public PostCreateException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public PostCreateException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class PostLikeException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public PostLikeException(String message) {
        super(message);
        this.errorCode = ErrorCode.POST_LIKE_FAILED;
    }
    
    public PostLikeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public PostLikeException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

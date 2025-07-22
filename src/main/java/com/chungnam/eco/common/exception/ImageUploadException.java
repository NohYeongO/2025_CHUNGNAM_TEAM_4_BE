package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class ImageUploadException extends RuntimeException {

    private final ErrorCode errorCode;

    public ImageUploadException() {
        this.errorCode = ErrorCode.IMAGE_UPLOAD_FAILED;
    }

    public ImageUploadException(String message) {
        super(message);
        this.errorCode = ErrorCode.IMAGE_UPLOAD_FAILED;
    }
}

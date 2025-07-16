package com.chungnam.eco.challenge.exception;

import com.chungnam.eco.common.exception.BusinessException;
import com.chungnam.eco.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidChallengeException extends BusinessException {
    public InvalidChallengeException(ErrorCode errorCode) {
        super(errorCode);
    }
}

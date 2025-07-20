package com.chungnam.eco.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 일반적인 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "S002", "잘못된 요청입니다."),

    // 엔티티 관련 에러
    INVALID_CHALLENGE(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "Challenge가 존재하지 않습니다."),
    INVALID_MISSION(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "Mission이 존재하지 않습니다."),
    
    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "미션을 찾을 수 없습니다."),

    // 인증/인가 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    MISSING_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "A002", "Authorization 헤더가 없거나 Bearer 토큰 형식이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

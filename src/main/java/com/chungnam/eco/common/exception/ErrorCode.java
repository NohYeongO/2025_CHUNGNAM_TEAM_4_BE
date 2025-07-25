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
    DATA_INTEGRITY_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, "S003", "데이터 정합성에 문제가 발생했습니다."),

    // 엔티티 관련 에러
    INVALID_CHALLENGE(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "Challenge가 존재하지 않습니다."),
    INVALID_MISSION(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "Mission이 존재하지 않습니다."),
    INVALID_MISSION_STATUS(HttpStatus.CONFLICT, "E003", "진행 중인 미션만 제출할 수 있습니다."),


    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "미션을 찾을 수 없습니다."),

    // 미션 선택 관련 에러
    MISSION_CHOICE_ERROR(HttpStatus.BAD_REQUEST, "M001", "미션 선택 중 오류가 발생했습니다."),
    INSUFFICIENT_MISSION(HttpStatus.INTERNAL_SERVER_ERROR, "M002", "현재 미션 목록이 부족합니다."),

    // 인증/인가 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    MISSING_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "A002", "Authorization 헤더가 없거나 Bearer 토큰 형식이 아닙니다."),

    // 이미지 업로드 관련 에러
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "I001", "이미지 업로드에 실패했습니다.");

    // AI 에러
    AI_MISSION_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI001", "AI 기반 미션 생성 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

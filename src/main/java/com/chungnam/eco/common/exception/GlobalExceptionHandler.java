package com.chungnam.eco.common.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * UserNotFoundException 처리
     */
    @ExceptionHandler(MissionNotFoundExcption.class)
    public ResponseEntity<ErrorResponse> handleMissionNotFoundException(
            UserNotFoundException e,
            HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    /**
     * UserNotFoundException 처리
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException e,
            HttpServletRequest request) {

        log.warn("User not found: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리 (주로 토큰 형식 오류)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletRequest request) {

        log.warn("Invalid argument: {}", e.getMessage());

        // Authorization 헤더 관련 오류인지 확인
        ErrorCode errorCode = e.getMessage().contains("Authorization")
                ? ErrorCode.MISSING_AUTHORIZATION_HEADER
                : ErrorCode.INVALID_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getCode(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * JWT 관련 예외 처리
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException e,
            HttpServletRequest request) {

        log.warn("JWT error: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INVALID_TOKEN.getCode(),
                "유효하지 않은 토큰입니다.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_TOKEN.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * InvalidTokenException 처리
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(
            InvalidTokenException e,
            HttpServletRequest request) {

        log.warn("Invalid token: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode().getCode(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 모든 예외를 처리하는 핸들러 (예시)
     *
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request) {

        log.error("Unexpected error occurred: ", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(errorResponse);
    }

    // 커스텀 예외 처리
    @ExceptionHandler(InvalidChallengeException.class)
    public ResponseEntity<ErrorResponse> HandleInvalidChallengeException(
            InvalidChallengeException e,
            HttpServletRequest request) {

        log.error("Invalid challenge : ", e);

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    // 커스텀 예외 처리
    @ExceptionHandler(InvalidMissionException.class)
    public ResponseEntity<ErrorResponse> HandleInvalidMissionException(
            InvalidMissionException e,
            HttpServletRequest request) {

        log.error("Invalid mission : ", e);

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * AI 생성 예외 처리
     */
    @ExceptionHandler(AICreationExceptions.class)
    public ResponseEntity<ErrorResponse> HandleAICreationException(
            AICreationExceptions e,
            HttpServletRequest request) {
        log.error("Invalid mission : ", e);

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}

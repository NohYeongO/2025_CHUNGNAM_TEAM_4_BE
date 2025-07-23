package com.chungnam.eco.common.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * MissionChoiceException 처리
     */
    @ExceptionHandler(MissionChoiceException.class)
    public ResponseEntity<ErrorResponse> handleMissionChoiceException(
            MissionChoiceException e,
            HttpServletRequest request) {

        log.warn("Mission choice error: {}", e.getMessage());

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
     * InsufficientMissionException 처리
     */
    @ExceptionHandler(InsufficientMissionException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientMissionException(
            InsufficientMissionException e,
            HttpServletRequest request) {

        log.warn("Insufficient mission: {}", e.getMessage());

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
     * MissionNotFoundExcption 처리
     */
    @ExceptionHandler(MissionNotFoundExcption.class)
    public ResponseEntity<ErrorResponse> handleMissionNotFoundException(
            MissionNotFoundExcption e,
            HttpServletRequest request) {

        log.warn("Mission not found: {}", e.getMessage());

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
     * UserNotFoundException 처리
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException e,
            HttpServletRequest request) {

        log.warn("User not found: {}", e.getMessage());

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



    // 커스텀 예외 처리
    @ExceptionHandler(InvalidChallengeException.class)
    public ResponseEntity<ErrorResponse> HandleInvalidChallengeException(
            InvalidChallengeException e,
            HttpServletRequest request) {

        log.warn("Invalid challenge : ", e);

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

        log.warn("Invalid mission : ", e);

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
     * ImageUploadException 처리
     */
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(
            ImageUploadException e,
            HttpServletRequest request) {

        log.warn("Image upload failed: {}", e.getMessage());

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
     * InvalidMissionStatusException 처리
     */
    @ExceptionHandler(InvalidMissionStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMissionStatusException(
            InvalidMissionStatusException e,
            HttpServletRequest request) {

        log.warn("Invalid mission status: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode().getCode(),
                e.getMessage() != null ? e.getMessage() : e.getErrorCode().getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

         /**
      * DataIntegrityException 처리
      */
     @ExceptionHandler(DataIntegrityException.class)
     public ResponseEntity<ErrorResponse> handleDataIntegrityException(
             DataIntegrityException e,
             HttpServletRequest request) {

         log.warn("Data integrity violation: {}", e.getMessage());

         ErrorResponse errorResponse = ErrorResponse.of(
                 e.getErrorCode().getCode(),
                 e.getMessage() != null ? e.getMessage() : e.getErrorCode().getMessage(),
                 request.getRequestURI()
         );

         return ResponseEntity
                 .status(e.getErrorCode().getHttpStatus())
                                 .body(errorResponse);
     }

     /**
      * MissingServletRequestPartException 처리 (@RequestPart 누락)
      */
     @ExceptionHandler(MissingServletRequestPartException.class)
     public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
             MissingServletRequestPartException e,
             HttpServletRequest request) {

         log.warn("Missing request part: {}", e.getMessage());

         ErrorResponse errorResponse = ErrorResponse.of(
                 ErrorCode.INVALID_REQUEST.getCode(),
                 "필수 요청 부분이 누락되었습니다: " + e.getRequestPartName(),
                 request.getRequestURI()
         );

         return ResponseEntity
                 .status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                 .body(errorResponse);
     }

     /**
      * MethodArgumentNotValidException 처리 (@Valid 검증 실패)
      */
     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
             MethodArgumentNotValidException e,
             HttpServletRequest request) {

         log.warn("Validation failed: {}", e.getMessage());

         // 첫 번째 validation 오류 메시지 추출
         String errorMessage = e.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .findFirst()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .orElse("입력값이 올바르지 않습니다.");

         ErrorResponse errorResponse = ErrorResponse.of(
                 ErrorCode.INVALID_REQUEST.getCode(),
                 errorMessage,
                 request.getRequestURI()
         );

         return ResponseEntity
                 .status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                 .body(errorResponse);
     }

     /**
      * ConstraintViolationException 처리 (@NotNull, @Size 등 개별 constraint 검증 실패)
      */
     @ExceptionHandler(ConstraintViolationException.class)
     public ResponseEntity<ErrorResponse> handleConstraintViolationException(
             ConstraintViolationException e,
             HttpServletRequest request) {

         log.warn("Constraint violation: {}", e.getMessage());

         // 첫 번째 constraint violation 메시지 추출
         String errorMessage = e.getConstraintViolations()
                 .stream()
                 .findFirst()
                 .map(violation -> violation.getMessage())
                 .orElse("제약 조건을 위반했습니다.");

         ErrorResponse errorResponse = ErrorResponse.of(
                 ErrorCode.INVALID_REQUEST.getCode(),
                 errorMessage,
                 request.getRequestURI()
         );

         return ResponseEntity
                 .status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                 .body(errorResponse);
     }

     /**
      * 모든 예외를 처리하는 핸들러
      *
      * @return ResponseEntity<ErrorResponse>
      */
     @ExceptionHandler(Exception.class)
     public ResponseEntity<ErrorResponse> handleException(
             Exception e,
             HttpServletRequest request) {

         log.warn("Unexpected error occurred: ", e);

         ErrorResponse errorResponse = ErrorResponse.of(
                 ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                 ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                 request.getRequestURI()
         );

         return ResponseEntity
                 .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                 .body(errorResponse);
     }
}
 
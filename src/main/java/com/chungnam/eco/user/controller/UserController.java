package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.exception.CustomException;
import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.user.controller.request.FindUserIdRequest;
import com.chungnam.eco.user.controller.request.ResetPasswordRequest;
import com.chungnam.eco.user.controller.request.SignInRequest;
import com.chungnam.eco.user.controller.request.SignUpRequest;
import com.chungnam.eco.user.controller.response.EmailCheckResponse;
import com.chungnam.eco.user.controller.response.FindUserIdResponse;
import com.chungnam.eco.user.controller.response.ResetPasswordResponse;
import com.chungnam.eco.user.controller.response.SignInResponse;
import com.chungnam.eco.user.controller.response.SignUpResponse;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.service.UserAppService;
import com.chungnam.eco.user.service.UserAuthService;
import com.chungnam.eco.user.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;
    private final UserAuthService userAuthService;
    private final VerificationService verificationService;

    /**
     * 이메일 중복 체크 API
     * @param email 중복 체크할 이메일
     * @return EmailCheckResponse(이메일 사용 가능 여부, Message)
     */
    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmailDuplicate(@RequestParam String email) {
        EmailCheckResponse response = userAuthService.checkEmailDuplicate(email);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입 API
     * @param request 회원가입 요청 정보 (이메일, 비밀번호, 닉네임)
     * @return SignUpResponse(회원가입 성공 여부, Message, 가입 email)
     */
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = userAuthService.signUp(request);
        return response.isSuccess() 
                ? ResponseEntity.ok(response) 
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * 로그인 API
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return SignInResponse(로그인 성공 여부, Message, JWT 토큰)
     */
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        try {
            SignInResponse response = userAuthService.signIn(request);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            SignInResponse response = SignInResponse.failure(e.getMessage());
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
        }
    }

    /**
     * 아이디 찾기 API
     * @param request 아이디 찾기 요청 정보 (닉네임)
     * @return FindUserIdResponse(찾기 성공 여부, Message, 마스킹된 이메일)
     */
    @PostMapping("/find-id")
    public ResponseEntity<FindUserIdResponse> findUserId(@Valid @RequestBody FindUserIdRequest request) {
        FindUserIdResponse response = userAuthService.findUserId(request);
        return response.isSuccess() 
                ? ResponseEntity.ok(response) 
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * 비밀번호 재설정 API
     * @param request 비밀번호 재설정 요청 정보 (인증 토큰, 새 비밀번호)
     * @return ResetPasswordResponse(재설정 성공 여부)
     */
    @PatchMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            ResetPasswordResponse response = verificationService.resetPassword(request);
            return response.isSuccess()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResetPasswordResponse.failure("비밀번호 재설정 중 오류가 발생했습니다: " + e.getMessage())
            );
        }
    }



    /**
     * 메인 페이지 정보 조회 API (인증 필요)
     * @return UserMainResponse(사용자 메인 페이지 정보)
     */
    @GetMapping("/main")
    public ResponseEntity<UserMainResponse> getMainPageInfo() {
        Long userId = AuthenticationHelper.getCurrentUserId();
        return ResponseEntity.ok(userAppService.getUserMainInfo(userId));
    }

    /**
     * 내가 참여한 미션 이력 조회 API (인증 필요)
     * @return 사용자가 참여한 모든 미션 및 상태
     */
    @GetMapping("/my-page/challenges")
    public ResponseEntity<?> getMyMissionParticipationHistory() {
        Long userId = AuthenticationHelper.getCurrentUserId();
        // TODO: 사용자가 참여한 모든 미션 + 상태(진행중, 심사중, 성공, 실패) 조회
        return ResponseEntity.ok().build();
    }
}

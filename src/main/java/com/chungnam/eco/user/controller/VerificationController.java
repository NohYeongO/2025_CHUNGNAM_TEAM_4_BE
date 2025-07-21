package com.chungnam.eco.user.controller;

import com.chungnam.eco.user.controller.request.ResetPasswordRequest;
import com.chungnam.eco.user.controller.request.VerificationRequest;
import com.chungnam.eco.user.controller.response.ResetPasswordResponse;
import com.chungnam.eco.user.controller.response.VerificationResponse;
import com.chungnam.eco.user.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    /**
     * 본인 확인 API (비밀번호 재설정을 위한)
     * @param request 본인 확인 요청 정보 (이메일, 닉네임)
     * @return VerificationResponse(확인 성공 여부, 인증 토큰)
     */
    @PostMapping("/api/verification/confirm")
    public ResponseEntity<VerificationResponse> confirmUser(@Valid @RequestBody VerificationRequest request) {
        VerificationResponse response = verificationService.verifyUser(request);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}

package com.chungnam.eco.user.service;

import com.chungnam.eco.common.exception.InvalidTokenException;
import com.chungnam.eco.user.controller.request.ResetPasswordRequest;
import com.chungnam.eco.user.controller.request.VerificationRequest;
import com.chungnam.eco.user.controller.response.ResetPasswordResponse;
import com.chungnam.eco.user.controller.response.VerificationResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.VerificationToken;
import com.chungnam.eco.user.repository.UserJPARepository;
import com.chungnam.eco.user.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UserJPARepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 본인 확인 (이메일 + 닉네임으로 사용자 검증 후 토큰 발급)
     */
    @Transactional
    public VerificationResponse verifyUser(VerificationRequest request) {
        // 이메일과 닉네임으로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .filter(u -> u.getNickname().equals(request.getNickname()))
                .orElse(null);

        if (user == null) {
            return VerificationResponse.failure("입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.");
        }

        // 기존 인증 토큰 삭제 (한 사용자당 하나의 토큰만 유지)
        verificationTokenRepository.deleteByUserId(user.getId());

        // 새로운 인증 토큰 생성 (15분 유효)
        String token = generateVerificationToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userId(user.getId())
                .expiryDate(expiryDate)
                .build();

        verificationTokenRepository.save(verificationToken);

        return VerificationResponse.success(token);
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        // 비밀번호 확인 검증
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResetPasswordResponse.failure("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 인증 토큰 검증
        VerificationToken verificationToken = verificationTokenRepository.findByToken(request.getVerificationToken())
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 인증 토큰입니다."));

        if (!verificationToken.isValid()) {
            return ResetPasswordResponse.failure("만료되었거나 이미 사용된 인증 토큰입니다.");
        }

        // 사용자 조회
        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new InvalidTokenException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경 (엔티티 메서드 활용)
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedPassword);
        userRepository.save(user);

        // 인증 토큰 사용 처리
        verificationToken.markAsUsed();
        verificationTokenRepository.save(verificationToken);

        log.info("사용자 {}의 비밀번호가 재설정되었습니다.", user.getEmail());

        return ResetPasswordResponse.success();
    }

    /**
     * 인증 토큰 생성
     */
    private String generateVerificationToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 만료된 인증 토큰 정리 (매일 자정 실행)
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        int deletedCount = verificationTokenRepository.deleteExpiredOrUsedTokens(LocalDateTime.now());
        log.info("만료된 인증 토큰 {} 개 삭제 완료", deletedCount);
    }
}
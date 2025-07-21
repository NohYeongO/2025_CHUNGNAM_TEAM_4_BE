package com.chungnam.eco.user.service;

import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.user.controller.request.FindUserIdRequest;
import com.chungnam.eco.user.controller.request.SignInRequest;
import com.chungnam.eco.user.controller.request.SignUpRequest;
import com.chungnam.eco.user.controller.response.EmailCheckResponse;
import com.chungnam.eco.user.controller.response.FindUserIdResponse;
import com.chungnam.eco.user.controller.response.SignInResponse;
import com.chungnam.eco.user.controller.response.SignUpResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import com.chungnam.eco.user.service.TokenService.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserJPARepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * 사용자 ID로 회원 조회
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    /**
     * 이메일 중복 체크 및 응답 생성
     */
    public EmailCheckResponse checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email)
                ? EmailCheckResponse.unavailable()
                : EmailCheckResponse.available();
    }

    /**
     * 회원가입 처리
     */
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            return SignUpResponse.failure("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            return SignUpResponse.failure("이미 사용 중인 닉네임입니다.");
        }

        User savedUser = userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build());

        return SignUpResponse.success(savedUser.getEmail());
    }

    /**
     * 로그인 처리 (RefreshToken 포함)
     */
    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    // 비밀번호 검증
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return SignInResponse.failure("비밀번호가 일치하지 않습니다.");
                    }

                    // 토큰 쌍 생성 (Access + Refresh)
                    TokenPair tokenPair = tokenService.createTokenPair(user);

                    return SignInResponse.success(
                            tokenPair.getAccessToken(),
                            tokenPair.getRefreshToken(),
                            user
                    );
                })
                .orElse(SignInResponse.failure("등록되지 않은 이메일입니다."));
    }

    /**
     * 아이디 찾기 (닉네임으로 이메일 찾기)
     */
    public FindUserIdResponse findUserId(FindUserIdRequest request) {
        return userRepository.findByNickname(request.getNickname())
                .map(user -> FindUserIdResponse.success(maskEmail(user.getEmail())))
                .orElse(FindUserIdResponse.notFound());
    }

    /**
     * 이메일 마스킹 처리
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        // 앞부분의 처음 1-2자만 보이게 하고 나머지는 ****로 마스킹
        String maskedLocal;
        if (localPart.length() <= 2) {
            maskedLocal = localPart.charAt(0) + "****";
        } else {
            maskedLocal = localPart.substring(0, 2) + "****";
        }

        return maskedLocal + "@" + domain;
    }
}
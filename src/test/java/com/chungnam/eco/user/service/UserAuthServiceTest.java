package com.chungnam.eco.user.service;

import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.user.controller.request.FindUserIdRequest;
import com.chungnam.eco.user.controller.request.SignInRequest;
import com.chungnam.eco.user.controller.request.SignUpRequest;
import com.chungnam.eco.user.controller.response.EmailCheckResponse;
import com.chungnam.eco.user.controller.response.FindUserIdResponse;
import com.chungnam.eco.user.controller.response.SignInResponse;
import com.chungnam.eco.user.controller.response.SignUpResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.UserRole;
import com.chungnam.eco.user.repository.UserJPARepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthService 단위 테스트")
class UserAuthServiceTest {

    @Mock
    private UserJPARepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserAuthService userAuthService;

    @Nested
    @DisplayName("getUserById 테스트")
    class GetUserByIdTest {

        @Test
        @DisplayName("사용자 ID로 사용자를 성공적으로 조회한다")
        void getUserById_Success() {
            // given
            Long userId = 1L;
            User mockUser = User.builder()
                    .email("test@example.com")
                    .password("encodedPassword")
                    .nickname("testUser")
                    .point(0)
                    .build();

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

            // when
            User result = userAuthService.getUserById(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getNickname()).isEqualTo("testUser");
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회시 UserNotFoundException이 발생한다")
        void getUserById_NotFound() {
            // given
            Long userId = 999L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userAuthService.getUserById(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다. ID: " + userId);
        }
    }

    @Nested
    @DisplayName("checkEmailDuplicate 테스트")
    class CheckEmailDuplicateTest {

        @Test
        @DisplayName("이메일이 이미 존재하는 경우 unavailable을 반환한다")
        void checkEmailDuplicate_Unavailable() {
            // given
            String email = "existing@example.com";
            given(userRepository.existsByEmail(email)).willReturn(true);

            // when
            EmailCheckResponse result = userAuthService.checkEmailDuplicate(email);

            // then
            assertThat(result.isExists()).isTrue();
            assertThat(result.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
        }

        @Test
        @DisplayName("이메일이 존재하지 않는 경우 available을 반환한다")
        void checkEmailDuplicate_Available() {
            // given
            String email = "new@example.com";
            given(userRepository.existsByEmail(email)).willReturn(false);

            // when
            EmailCheckResponse result = userAuthService.checkEmailDuplicate(email);

            // then
            assertThat(result.isExists()).isFalse();
            assertThat(result.getMessage()).isEqualTo("사용 가능한 이메일입니다.");
        }
    }

    @Nested
    @DisplayName("signUp 테스트")
    class SignUpTest {

        @Test
        @DisplayName("모든 조건이 만족되면 회원가입에 성공한다")
        void signUp_Success() {
            // given
            SignUpRequest request = new SignUpRequest();
            ReflectionTestUtils.setField(request, "email", "new@example.com");
            ReflectionTestUtils.setField(request, "password", "password123!");
            ReflectionTestUtils.setField(request, "nickname", "newUser");

            User savedUser = User.builder()
                    .email("new@example.com")
                    .password("encodedPassword")
                    .nickname("newUser")
                    .point(0)
                    .build();

            given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
            given(userRepository.existsByNickname(request.getNickname())).willReturn(false);
            given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            SignUpResponse result = userAuthService.signUp(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
            assertThat(result.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("이메일이 이미 존재하는 경우 회원가입에 실패한다")
        void signUp_EmailDuplicate() {
            // given
            SignUpRequest request = new SignUpRequest();
            ReflectionTestUtils.setField(request, "email", "existing@example.com");
            ReflectionTestUtils.setField(request, "password", "password123!");
            ReflectionTestUtils.setField(request, "nickname", "newUser");

            given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

            // when
            SignUpResponse result = userAuthService.signUp(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
            assertThat(result.getEmail()).isNull();
        }

        @Test
        @DisplayName("닉네임이 이미 존재하는 경우 회원가입에 실패한다")
        void signUp_NicknameDuplicate() {
            // given
            SignUpRequest request = new SignUpRequest();
            ReflectionTestUtils.setField(request, "email", "new@example.com");
            ReflectionTestUtils.setField(request, "password", "password123!");
            ReflectionTestUtils.setField(request, "nickname", "existingUser");

            given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
            given(userRepository.existsByNickname(request.getNickname())).willReturn(true);

            // when
            SignUpResponse result = userAuthService.signUp(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getMessage()).isEqualTo("이미 사용 중인 닉네임입니다.");
            assertThat(result.getEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("signIn 테스트")
    class SignInTest {

        @Test
        @DisplayName("올바른 이메일과 비밀번호로 로그인에 성공한다")
        void signIn_Success() {
            // given
            SignInRequest request = new SignInRequest();
            ReflectionTestUtils.setField(request, "email", "test@example.com");
            ReflectionTestUtils.setField(request, "password", "password123!");

            User mockUser = User.builder()
                    .email("test@example.com")
                    .password("encodedPassword")
                    .nickname("testUser")
                    .role(UserRole.USER)
                    .point(100)
                    .build();

            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(true);
            given(jwtProvider.generateAccessToken(mockUser.getId(), mockUser.getRole().name())).willReturn("accessToken");

            // when
            SignInResponse result = userAuthService.signIn(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("로그인이 완료되었습니다.");
            assertThat(result.getAccessToken()).isEqualTo("accessToken");
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인시 실패한다")
        void signIn_UserNotFound() {
            // given
            SignInRequest request = new SignInRequest();
            ReflectionTestUtils.setField(request, "email", "nonexistent@example.com");
            ReflectionTestUtils.setField(request, "password", "password123!");

            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

            // when
            SignInResponse result = userAuthService.signIn(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getMessage()).isEqualTo("등록되지 않은 이메일입니다.");
            assertThat(result.getAccessToken()).isNull();
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
        void signIn_WrongPassword() {
            // given
            SignInRequest request = new SignInRequest();
            ReflectionTestUtils.setField(request, "email", "test@example.com");
            ReflectionTestUtils.setField(request, "password", "wrongPassword");

            User mockUser = User.builder()
                    .email("test@example.com")
                    .password("encodedPassword")
                    .nickname("testUser")
                    .point(0)
                    .build();

            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(false);

            // when
            SignInResponse result = userAuthService.signIn(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
            assertThat(result.getAccessToken()).isNull();
        }
    }

    @Nested
    @DisplayName("findUserId 테스트")
    class FindUserIdTest {

        @Test
        @DisplayName("닉네임으로 사용자를 찾으면 마스킹된 이메일을 반환한다")
        void findUserId_Success() {
            // given
            FindUserIdRequest request = FindUserIdRequest.builder()
                    .nickname("testUser")
                    .build();
            
            User mockUser = User.builder()
                    .email("testuser@example.com")
                    .nickname("testUser")
                    .point(0)
                    .build();

            given(userRepository.findByNickname("testUser")).willReturn(Optional.of(mockUser));

            // when
            FindUserIdResponse result = userAuthService.findUserId(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("회원님의 아이디를 찾았습니다.");
            assertThat(result.getMaskedEmail()).isEqualTo("te****@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 닉네임으로 조회시 실패한다")
        void findUserId_NotFound() {
            // given
            FindUserIdRequest request = FindUserIdRequest.builder()
                    .nickname("nonexistentUser")
                    .build();
            
            given(userRepository.findByNickname("nonexistentUser")).willReturn(Optional.empty());

            // when
            FindUserIdResponse result = userAuthService.findUserId(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
            assertThat(result.getMaskedEmail()).isNull();
        }

        @Test
        @DisplayName("다양한 이메일 형식에 대한 마스킹 테스트")
        void findUserId_EmailMaskingVariations() {
            // given - 짧은 이메일
            FindUserIdRequest request1 = FindUserIdRequest.builder()
                    .nickname("user1")
                    .build();
            
            User mockUser1 = User.builder()
                    .email("ab@test.com")
                    .nickname("user1")
                    .point(0)
                    .build();

            given(userRepository.findByNickname("user1")).willReturn(Optional.of(mockUser1));

            // when
            FindUserIdResponse result1 = userAuthService.findUserId(request1);

            // then
            assertThat(result1.getMaskedEmail()).isEqualTo("a****@test.com");

            // given - 긴 이메일
            FindUserIdRequest request2 = FindUserIdRequest.builder()
                    .nickname("user2")
                    .build();
            
            User mockUser2 = User.builder()
                    .email("verylongemail@example.com")
                    .nickname("user2")
                    .point(0)
                    .build();

            given(userRepository.findByNickname("user2")).willReturn(Optional.of(mockUser2));

            // when
            FindUserIdResponse result2 = userAuthService.findUserId(request2);

            // then
            assertThat(result2.getMaskedEmail()).isEqualTo("ve****@example.com");
        }
    }
}

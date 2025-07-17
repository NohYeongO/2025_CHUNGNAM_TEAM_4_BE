package com.chungnam.eco.user.service;

import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * UserService는 회원과 관련한 핵심 비즈니스 로직을 수행하는 서비스 계층 클래스입니다.
 * 컨트롤러와 리포지토리 사이에서 실제 회원가입, 로그인, 이메일 중복 검증 등의 처리를 담당합니다.
 */
@Service // 스프링에서 서비스 컴포넌트임을 선언, DI(의존성 주입) 및 트랜잭션 관리 대상
public class UserService {

    private final UserJPARepository userRepo;         // DB 작업을 담당하는 JPA Repository 주입
    private final PasswordEncoder passwordEncoder;    // 비밀번호 암호화를 위한 Spring Security의 PasswordEncoder 주입

    /**
     * 생성자 기반 의존성 주입 (Spring 컨테이너가 자동으로 객체를 주입해줌)
     * @param userRepo 유저 정보 DB CRUD 담당
     * @param passwordEncoder 비밀번호 암호화 및 검증 담당
     */
    @Autowired
    public UserService(UserJPARepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 이메일 중복 여부 검사
     * @param email 대상 이메일
     * @return 이미 DB에 존재하면 true, 없으면 false 반환
     * - 회원가입 시 중복 확인용으로 사용
     */
    public boolean checkEmailDuplicate(String email) {
        return userRepo.existsByEmail(email);
    }

    /**
     * 회원가입 처리
     * - 비밀번호는 암호화하여 저장 보안 강화
     * - 회원 생성/수정일, 초기 포인트 값을 설정 후 DB 저장
     * @param user 회원 가입을 위한 User 객체 (비밀번호 평문 포함)
     * @return 저장된 User 엔티티 (ID 포함)
     */
    public User signUp(User user) {
        // 비밀번호를 Spring 보안 표준 암호화 방식으로 인코딩
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 생성 및 수정 시간 현재 시각으로 세팅
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        // 초기 포인트 0으로 할당
        user.setPoint(0);
        // User 엔티티를 DB에 저장하고 저장된 객체 반환
        return userRepo.save(user);
    }

    /**
     * 로그인 인증 검증
     * - 이메일로 회원 조회 후, 입력받은 비밀번호 평문과 저장된 암호화 비밀번호 비교
     * @param email 로그인 시 입력한 이메일
     * @param rawPassword 로그인 시 입력한 평문 비밀번호
     * @return 인증 성공 시 Optional<User> 포함, 실패 시 빈 Optional 반환
     */
    public Optional<User> validateUser(String email, String rawPassword) {
        return userRepo.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    /**
     * 사용자 이메일로 회원 조회
     * @param email 조회할 회원 이메일
     * @return Optional<User> 반환, 없으면 Optional.empty()
     */
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}

package com.chungnam.eco.user.repository;

import com.chungnam.eco.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * UserJPARepository는 User 엔티티를 위한 JPA 데이터 접근 계층 인터페이스입니다.
 *
 * 주요 용도 및 설계 의도:
 * - Spring Data JPA의 JpaRepository를 상속해 DB의 user 테이블과 자동으로 연동(Insert, Update, Delete, Find 등 기본 메서드 제공)
 * - 회원 정보를 이메일 기준으로 효율적으로 조회하거나, 이메일 중복 여부를 검사할 수 있도록 커스텀 메서드 추가
 */
public interface UserJPARepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 회원 정보를 조회하는 메서드
     * - 입력한 이메일을 가진 User 엔티티(회원)가 존재하는지 Optional로 반환
     * - 회원가입·로그인 등에서 유저 찾기에 활용
     *
     * @param email 조회할 사용자의 이메일
     * @return 그 이메일을 가진 User, 없으면 Optional.empty()
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일로 회원(이메일 중복) 여부 확인 메서드
     * - 이미 해당 이메일로 가입된 회원이 있는지 true/false로 바로 확인
     * - 회원가입 시 중복 검사 등에서 사용
     *
     * @param email 검사할 이메일
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByEmail(String email);
}

package com.chungnam.eco.user.repository;

import com.chungnam.eco.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * RefreshToken 엔티티를 DB에서 손쉽게 CRUD(생성/조회/수정/삭제)할 수 있도록
 * JPA가 제공하는 표준 Repository 인터페이스입니다.
 *
 * 주요 목적:
 * - 리프레시 토큰(RefreshToken) 엔티티를 이메일(user 계정) 기준으로 조회/삭제
 * - 곧바로 JPA의 save(), findById(), findAll() 등 다양한 기능도 함께 지원
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * DB에서 특정 사용자의 이메일로 리프레시 토큰을 찾는 커스텀 메서드.
     * Optional<RefreshToken>을 반환하여, 토큰이 없을 경우 null 대신 안전하게 처리 가능.
     *
     * @param email 사용자 고유 이메일 (RefreshToken에 unique로 저장)
     * @return 해당 사용자의 RefreshToken 객체 (없으면 Optional.empty())
     */
    Optional<RefreshToken> findByEmail(String email);

    /**
     * 특정 이메일을 가진 사용자의 리프레시 토큰을 DB에서 삭제하는 메서드.
     * Spring Data JPA의 메서드 네이밍 룰로 자동 쿼리 생성됨.
     *
     * @param email 삭제할 사용자의 이메일
     */
    void deleteByEmail(String email);
}

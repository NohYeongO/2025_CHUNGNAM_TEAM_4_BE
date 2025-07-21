package com.chungnam.eco.user.repository;

import com.chungnam.eco.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰으로 RefreshToken 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID로 RefreshToken 조회
     */
    Optional<RefreshToken> findByUserId(Long userId);

    /**
     * 사용자 ID로 RefreshToken 삭제
     */
    void deleteByUserId(Long userId);

    /**
     * 만료된 토큰들 일괄 삭제
     */
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiryDate < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * 토큰 존재 여부 확인
     */
    boolean existsByToken(String token);
}

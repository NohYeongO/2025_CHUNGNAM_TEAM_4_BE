package com.chungnam.eco.user.repository;

import com.chungnam.eco.user.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * 토큰으로 VerificationToken 조회
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * 사용자 ID로 VerificationToken 조회 (가장 최근 것)
     */
    Optional<VerificationToken> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자 ID로 기존 토큰들 삭제
     */
    void deleteByUserId(Long userId);

    /**
     * 만료된 토큰들 일괄 삭제
     */
    @Modifying
    @Query("DELETE FROM VerificationToken v WHERE v.expiryDate < :now OR v.used = true")
    int deleteExpiredOrUsedTokens(@Param("now") LocalDateTime now);

    /**
     * 토큰 존재 여부 확인
     */
    boolean existsByToken(String token);
}

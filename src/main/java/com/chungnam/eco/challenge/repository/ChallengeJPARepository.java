package com.chungnam.eco.challenge.repository;

import com.chungnam.eco.challenge.domain.Challenge;
import com.chungnam.eco.challenge.domain.ChallengeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeJPARepository extends JpaRepository<Challenge, Long> {
    Page<Challenge> findByChallengeStatus(ChallengeStatus status, Pageable pageable);
}

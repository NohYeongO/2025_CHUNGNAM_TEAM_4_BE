package com.chungnam.eco.challenge.repository;

import com.chungnam.eco.challenge.domain.ChallengeImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeImageJPARepository extends JpaRepository<ChallengeImage, Long> {
    @Query("SELECT ci FROM ChallengeImage ci WHERE ci.challenge.id = :challengeId ORDER BY ci.sort ASC")
    List<ChallengeImage> findByChallengeIdOrderBySort(@Param("challengeId") Long challengeId);
}

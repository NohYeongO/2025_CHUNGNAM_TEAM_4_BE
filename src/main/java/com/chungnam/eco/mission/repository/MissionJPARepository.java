package com.chungnam.eco.mission.repository;

import com.chungnam.eco.mission.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionJPARepository extends JpaRepository<Mission, Long> {

    /**
     * 활성화된 미션 중에서 랜덤으로 조회
     *
     * @param type 미션 타입 (DAILY/WEEKLY)
     * @param status 미션 상태 (ACTIVE)
     * @param limit 조회할 개수
     * @return 랜덤 선택된 활성 미션 목록
     */
    @Query(value = "SELECT * FROM mission m WHERE m.type = :type AND m.status = :status ORDER BY RAND() LIMIT :limit",
           nativeQuery = true)
    List<Mission> findRandomActiveMissions(@Param("type") String type,
                                         @Param("status") String status,
                                         @Param("limit") int limit);
}

package com.chungnam.eco.point.repository;

import com.chungnam.eco.point.domain.Point;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointJPARepository extends JpaRepository<Point, Long> {
    // 최근 사용한 내역부터 리스트 조회
    List<Point> findByUserIdOrderByCreatedAtDesc(Long userId);
}

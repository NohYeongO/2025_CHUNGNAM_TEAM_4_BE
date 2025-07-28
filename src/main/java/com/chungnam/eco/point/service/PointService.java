package com.chungnam.eco.point.service;

import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.point.domain.Point;
import com.chungnam.eco.point.repository.PointJPARepository;
import com.chungnam.eco.point.service.dto.PointDto;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 포인트 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 포인트 사용
 * - 포인트 사용 내역 조회
 * - 현재 보유 포인트 조회
 * <p>
 * 유저 조회 시 비관적 락을 사용하여 동시성 문제를 방지합니다.
 */
@Service
@RequiredArgsConstructor
public class PointService {

    private final UserJPARepository userRepository;
    private final PointJPARepository pointRepository;


    /**
     * 특정 유저의 포인트 사용 내역을 조회합니다.
     *
     * @param userId 유저 ID
     * @return 포인트 사용 내역 리스트 (최신순 정렬)
     */
    @Transactional(readOnly = true)
    public List<PointDto> getPointHistory(Long userId) {
        List<Point> pointList = pointRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return pointList.stream()
                .map(PointDto::from)
                .toList();
    }

    /**
     * 특정 유저의 현재 보유 포인트를 반환합니다.
     *
     * @param userId 유저 ID
     * @return 현재 보유 포인트
     */
    @Transactional(readOnly = true)
    public Integer getUserPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다 : " + userId));
        return user.getPoint();
    }
}

package com.chungnam.eco.point.service;


import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.point.domain.Point;
import com.chungnam.eco.point.repository.PointJPARepository;
import com.chungnam.eco.point.service.dto.PointDto;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayService {
    private final UserJPARepository userRepository;
    private final PointJPARepository pointRepository;
    
    /**
     * ID를 기반으로 유저를 조회하며, 비관적 락을 사용합니다.
     *
     * @param id 유저 ID
     * @return 조회된 유저 객체
     */
    public User findUser(Long id) {
        return userRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없음 : " + id));
    }

    /**
     * 포인트 내역을 저장합니다.
     *
     * @param point 저장할 포인트 객체
     * @return 저장된 포인트 객체
     */
    public Point savePoint(Point point) {
        return pointRepository.save(point);
    }

    /**
     * 특정 유저가 포인트를 사용하는 로직입니다.
     * - 유저 조회 및 포인트 차감
     * - 포인트 사용 내역 저장
     *
     * @param userId     유저 ID
     * @param shopName   포인트 사용 장소 이름
     * @param usedPoints 사용한 포인트 양
     * @return 사용된 포인트에 대한 DTO
     */
    @Transactional
    public PointDto usedPoints(Long userId, String shopName, Integer usedPoints) {
        User user = findUser(userId);
        user.deductPoint(usedPoints); // 포인트 차감

        Point point = Point.builder()
                .usedPoints(usedPoints)
                .user(user)
                .shopName(shopName)
                .totalPoints(user.getPoint())
                .build();

        Point savedPoint = savePoint(point);

        return PointDto.from(savedPoint);
    }
}

package com.chungnam.eco.mission.service;

import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionCacheService {

    private final CacheManager caffeineCacheManager;
    private final CacheManager redisCacheManager;

    @CacheEvict(value = "dailyMissions", key = "#userInfo.userId", cacheManager = "caffeineCacheManager")
    public void evictDailyMissionsCache(UserInfoDto userInfo) {
        log.info("일일 미션 캐시 무효화 - 사용자 ID: {}", userInfo.getUserId());
    }

    @CacheEvict(value = "weeklyMissions", key = "#userInfo.userId", cacheManager = "redisCacheManager")
    public void evictWeeklyMissionsCache(UserInfoDto userInfo) {
        log.info("주간 미션 캐시 무효화 - 사용자 ID: {}", userInfo.getUserId());
    }

    /**
     * 모든 미션 캐시 무효화
     */
    public void evictAllMissionsCache(UserInfoDto userInfo) {
        try {
            // 별도 서비스에서 호출하므로 AOP 프록시가 정상 작동
            evictDailyMissionsCache(userInfo);
            evictWeeklyMissionsCache(userInfo);
            log.info("모든 미션 캐시 무효화 완료 - 사용자 ID: {}", userInfo.getUserId());
        } catch (Exception e) {
            log.error("캐시 무효화 중 오류 발생 - 사용자 ID: {}, 오류: {}", userInfo.getUserId(), e.getMessage());
            throw e;
        }
    }
} 
package com.chungnam.eco.common.config;

import lombok.Getter;

import java.time.Duration;

/**
 * 캐시 타입 정의
 * - 일일 미션: Caffeine (인메모리) - 자정까지 TTL
 * - 주간 미션: Redis - 일요일 자정까지 TTL
 */
@Getter
public enum CacheType {

    // 일일 미션 (Caffeine 캐시 - 자정까지)
    DAILY_MISSIONS("dailyMissions", "local", CacheConfig.getTtlUntilMidnight()),
    // 주간 미션 (Redis 캐시 - 다음 주 일요일 자정까지)
    WEEKLY_MISSIONS("weeklyMissions", "redis", CacheConfig.getTtlUntilSundayMidnight());

    private final String cacheName;
    private final String cacheStore;
    private final Duration expiredAfterWrite;

    CacheType(String cacheName, String cacheStore, Duration expiredAfterWrite) {
        this.cacheName = cacheName;
        this.cacheStore = cacheStore;
        this.expiredAfterWrite = expiredAfterWrite;
    }
}

package com.chungnam.eco.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 캐시 설정
 * - 일일 미션: Caffeine (인메모리) - 자정까지 TTL
 * - 주간 미션: Redis - 일요일 자정까지 TTL
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    /**
     * Redis 캐시 매니저 설정
     */
    @Bean(name = "redisCacheManager")
    public CacheManager redisCacheManager() {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.registerModule(new JavaTimeModule());

        Map<String, RedisCacheConfiguration> cacheConfigMap = Arrays.stream(CacheType.values())
                .filter(cacheType -> "redis".equals(cacheType.getCacheStore()))
                .collect(Collectors.toMap(
                        CacheType::getCacheName,
                        cacheType -> RedisCacheConfiguration.defaultCacheConfig()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(redisObjectMapper, Object.class)))
                                .disableCachingNullValues()
                                .entryTtl(cacheType.getExpiredAfterWrite())
                                .computePrefixWith(cacheName -> "cache:" + cacheName + ":")
                ));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigMap)
                .build();
    }

    /**
     * Caffeine 캐시 매니저 설정
     */
    @Bean(name = "caffeineCacheManager")
    @Primary
    public CacheManager caffeineCacheManager() {
        List<CaffeineCache> caffeineCaches = Arrays.stream(CacheType.values())
                .filter(cacheType -> "local".equals(cacheType.getCacheStore()))
                .map(cache -> new CaffeineCache(
                        cache.getCacheName(),
                        Caffeine.newBuilder()
                                .expireAfterWrite(cache.getExpiredAfterWrite())
                                .maximumSize(1000)
                                .build()
                ))
                .toList();

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(new ArrayList<>(caffeineCaches));
        return cacheManager;
    }

    /**
     * 자정까지의 시간 계산 (일일 미션용)
     */
    public static Duration getTtlUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atTime(LocalTime.MIDNIGHT);
        return Duration.between(now, midnight);
    }

    /**
     * 이번 주 일요일 자정까지의 시간 계산 (주간 미션용)
     */
    public static Duration getTtlUntilSundayMidnight() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime sundayMidnight = now
                .with(java.time.DayOfWeek.SUNDAY)
                .plusWeeks(1) // 다음 주 일요일
                .with(LocalTime.MIDNIGHT);

        return Duration.between(now, sundayMidnight);
    }
}

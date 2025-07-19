package com.chungnam.eco.user.service;

import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
class UserAppServiceCacheTest extends TestContainerConfig {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserJPARepository userRepository;

    @Autowired
    @Qualifier("caffeineCacheManager")
    private CacheManager caffeineCacheManager;

    @Autowired
    @Qualifier("redisCacheManager")
    private CacheManager redisCacheManager;

    @MockitoBean
    private UserMissionJPARepository userMissionRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        caffeineCacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(caffeineCacheManager.getCache(cacheName)).clear());
        redisCacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(redisCacheManager.getCache(cacheName)).clear());
        testUser = userRepository.findById(1L).orElse(null);
    }


    @Test
    @DisplayName("사용자 메인 정보 캐시 테스트 - 같은 사용자 ID로 두 번 호출 시 실제 서비스는 한 번만 호출")
    void getUserMainInfo_CacheTest() {
        // given
        Long userId = testUser.getId();

        // when - 같은 사용자 ID로 두 번 호출
        UserMainResponse firstCall = userAppService.getUserMainInfo(userId);
        UserMainResponse secondCall = userAppService.getUserMainInfo(userId);

        // then
        assertThat(firstCall).isNotNull();
        assertThat(secondCall).isNotNull();
        assertThat(firstCall.getUserInfo().getNickname()).isEqualTo(secondCall.getUserInfo().getNickname());
        assertThat(firstCall.getUserInfo().getPoint()).isEqualTo(secondCall.getUserInfo().getPoint());

        // 캐시가 동작한다면 미션 조회는 각각 한 번씩만 호출되어야 함
        verify(userMissionRepository, times(1)).findByUserAndMissionTypeAndStatusIn(
                testUser, MissionType.DAILY, List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED));
        verify(userMissionRepository, times(1)).findByUserAndMissionTypeAndStatusIn(
                testUser, MissionType.WEEKLY, List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED));
    }

    @Test
    @DisplayName("사용자 메인 정보 캐시 테스트 - 여러 번 호출해도 캐시로 인해 실제 호출은 한 번")
    void getUserMainInfo_MultipleCalls_CacheTest() {
        // given
        Long userId = testUser.getId();
        int callCount = 5;

        // when - 같은 사용자 ID로 여러 번 호출
        for (int i = 0; i < callCount; i++) {
            userAppService.getUserMainInfo(userId);
        }

        // then
        verify(userMissionRepository, times(1)).findByUserAndMissionTypeAndStatusIn(
                testUser, MissionType.DAILY, List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED));
        verify(userMissionRepository, times(1)).findByUserAndMissionTypeAndStatusIn(
                testUser, MissionType.WEEKLY, List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED));
    }
}

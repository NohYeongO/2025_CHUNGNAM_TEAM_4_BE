package com.chungnam.eco.user.service;

import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.mission.domain.*;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        caffeineCacheManager.getCacheNames().forEach(cacheName ->
            Objects.requireNonNull(caffeineCacheManager.getCache(cacheName)).clear());
        redisCacheManager.getCacheNames().forEach(cacheName ->
            Objects.requireNonNull(redisCacheManager.getCache(cacheName)).clear());

        testUser = userRepository.findById(1L).orElse(null);

        // Mock 객체 생성 (Mockito.mock 사용)
        Mission dailyMission = Mockito.mock(Mission.class);
        when(dailyMission.getId()).thenReturn(1L);
        when(dailyMission.getTitle()).thenReturn("일일 테스트 미션");
        when(dailyMission.getType()).thenReturn(MissionType.DAILY);
        when(dailyMission.getDescription()).thenReturn("테스트용 일일 미션");
        when(dailyMission.getCategory()).thenReturn(MissionCategory.DAILY_HABIT);
        when(dailyMission.getLevel()).thenReturn(MissionLevel.LOW);
        when(dailyMission.getStatus()).thenReturn(MissionStatus.ACTIVATE);
        when(dailyMission.getRewardPoints()).thenReturn(10);

        Mission weeklyMission = Mockito.mock(Mission.class);
        when(weeklyMission.getId()).thenReturn(2L);
        when(weeklyMission.getTitle()).thenReturn("주간 테스트 미션");
        when(weeklyMission.getType()).thenReturn(MissionType.WEEKLY);
        when(weeklyMission.getDescription()).thenReturn("테스트용 주간 미션");
        when(weeklyMission.getCategory()).thenReturn(MissionCategory.ECO_CONSUMPTION);
        when(weeklyMission.getLevel()).thenReturn(MissionLevel.MIDDLE);
        when(weeklyMission.getStatus()).thenReturn(MissionStatus.ACTIVATE);
        when(weeklyMission.getRewardPoints()).thenReturn(50);

        // UserMission Mock 객체 생성
        UserMission mockDailyMission = Mockito.mock(UserMission.class);
        when(mockDailyMission.getId()).thenReturn(1L);
        when(mockDailyMission.getUser()).thenReturn(testUser);
        when(mockDailyMission.getMission()).thenReturn(dailyMission);
        when(mockDailyMission.getMissionType()).thenReturn(MissionType.DAILY);
        when(mockDailyMission.getStatus()).thenReturn(UserMissionStatus.IN_PROGRESS);

        UserMission mockWeeklyMission = Mockito.mock(UserMission.class);
        when(mockWeeklyMission.getId()).thenReturn(2L);
        when(mockWeeklyMission.getUser()).thenReturn(testUser);
        when(mockWeeklyMission.getMission()).thenReturn(weeklyMission);
        when(mockWeeklyMission.getMissionType()).thenReturn(MissionType.WEEKLY);
        when(mockWeeklyMission.getStatus()).thenReturn(UserMissionStatus.IN_PROGRESS);

        // Mock Repository 설정 - 실제 데이터를 반환하도록 설정
        when(userMissionRepository.findByUserAndMissionTypeAndStatusIn(
                eq(testUser),
                eq(MissionType.DAILY),
                eq(List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED))
        )).thenReturn(List.of(mockDailyMission));

        when(userMissionRepository.findByUserAndMissionTypeAndStatusIn(
                eq(testUser),
                eq(MissionType.WEEKLY),
                eq(List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED))
        )).thenReturn(List.of(mockWeeklyMission));
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

package com.chungnam.eco.user.service;

import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.mission.domain.MissionStatus;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.repository.MissionJPARepository;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.UserRole;
import com.chungnam.eco.user.repository.UserJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
class UserAppServiceCacheTest extends TestContainerConfig {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserJPARepository userRepository;

    @MockitoBean
    private MissionJPARepository missionRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .nickname("테스트 사용자")
                .email("test@example.com")
                .password("password")
                .role(UserRole.USER)
                .point(0)
                .build();
        testUser = userRepository.save(testUser);
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
        verify(missionRepository, times(1)).findRandomActiveMissions(
                MissionType.DAILY.name(), MissionStatus.ACTIVATE.name(), 3);
        verify(missionRepository, times(1)).findRandomActiveMissions(
                MissionType.WEEKLY.name(), MissionStatus.ACTIVATE.name(), 2);
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

        // then - 실제 미션 조회는 첫 번째 호출에서만 실행됨
        verify(missionRepository, times(1)).findRandomActiveMissions(
                MissionType.DAILY.name(), MissionStatus.ACTIVATE.name(), 3);
        verify(missionRepository, times(1)).findRandomActiveMissions(
                MissionType.WEEKLY.name(), MissionStatus.ACTIVATE.name(), 2);
    }
}

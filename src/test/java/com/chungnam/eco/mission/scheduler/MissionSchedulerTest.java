package com.chungnam.eco.mission.scheduler;

import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class MissionSchedulerTest extends TestContainerConfig {

    @Autowired
    private MissionScheduler missionScheduler;

    @Autowired
    private UserMissionJPARepository userMissionRepository;

    @Test
    @DisplayName("일일 미션 스케줄러 테스트: 스케줄러 실행 시 모든 일일 미션(IN_PROGRESS, SUBMITTED)이 EXPIRED 상태로 변경된다.")
    void expireDailyMissions_WithTestData_Test() {
        // given
        long dailyInProgressCount = userMissionRepository.findAll().stream()
                .filter(um -> um.getMissionType() == MissionType.DAILY && (um.getStatus() == UserMissionStatus.IN_PROGRESS || um.getStatus() == UserMissionStatus.SUBMITTED))
                .count();
        assertThat(dailyInProgressCount).isEqualTo(3);

        // when
        missionScheduler.expireDailyMissions();

        // then
        List<UserMission> allMissions = userMissionRepository.findAll();
        // 일일 미션들은 모두 EXPIRED 상태여야 함
        allMissions.stream()
                .filter(um -> um.getMissionType() == MissionType.DAILY)
                .forEach(um -> assertThat(um.getStatus()).isEqualTo(UserMissionStatus.EXPIRED));

        // 주간 미션들은 상태가 변경되지 않아야 함
        allMissions.stream()
                .filter(um -> um.getMissionType() == MissionType.WEEKLY)
                .forEach(um -> assertThat(um.getStatus()).isNotEqualTo(UserMissionStatus.EXPIRED));
    }

    @Test
    @DisplayName("주간 미션 스케줄러 테스트: 스케줄러 실행 시 모든 주간 미션(IN_PROGRESS, SUBMITTED)이 EXPIRED 상태로 변경된다.")
    void expireWeeklyMissions_WithTestData_Test() {
        // given
        long weeklyInProgressCount = userMissionRepository.findAll().stream()
                .filter(um -> um.getMissionType() == MissionType.WEEKLY && (um.getStatus() == UserMissionStatus.IN_PROGRESS || um.getStatus() == UserMissionStatus.SUBMITTED))
                .count();
        assertThat(weeklyInProgressCount).isEqualTo(1);

        // when
        missionScheduler.expireWeeklyMissions();

        // then
        List<UserMission> allMissions = userMissionRepository.findAll();
        // 주간 미션들은 모두 EXPIRED 상태여야 함
        allMissions.stream()
                .filter(um -> um.getMissionType() == MissionType.WEEKLY)
                .forEach(um -> assertThat(um.getStatus()).isEqualTo(UserMissionStatus.EXPIRED));

        // 일일 미션들은 상태가 변경되지 않아야 함
        allMissions.stream()
                .filter(um -> um.getMissionType() == MissionType.DAILY)
                .forEach(um -> assertThat(um.getStatus()).isNotEqualTo(UserMissionStatus.EXPIRED));
    }
}

package com.chungnam.eco.mission.scheduler;

import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import com.chungnam.eco.mission.repository.UserMissionJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionScheduler {

    private final UserMissionJPARepository userMissionRepository;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void expireDailyMissions() {
        log.info("일일 미션 만료 스케줄러 시작");
        int updatedCount = userMissionRepository.updateStatusForMissions(
                MissionType.DAILY,
                List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED),
                UserMissionStatus.EXPIRED
        );
        log.info("만료된 일일 미션 개수: {}", updatedCount);
    }

    // 매주 월요일 자정 실행
    @Scheduled(cron = "0 0 0 * * MON")
    public void expireWeeklyMissions() {
        log.info("주간 미션 만료 스케줄러 시작");
        int updatedCount = userMissionRepository.updateStatusForMissions(
                MissionType.WEEKLY,
                List.of(UserMissionStatus.IN_PROGRESS, UserMissionStatus.SUBMITTED),
                UserMissionStatus.EXPIRED
        );
        log.info("만료된 주간 미션 개수: {}", updatedCount);
    }
} 
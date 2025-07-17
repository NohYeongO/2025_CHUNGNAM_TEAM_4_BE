package com.chungnam.eco.common.notification;

import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.config.TestContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Discord 알림 전송 테스트
 * 실제 Discord 웹훅으로 메시지가 전송되는지 확인
 */
class DiscordNotificationTest extends TestContainerConfig {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotificationTest.class);

    @Autowired
    private DiscordNotificationService discordNotificationService;

    @Test
    @DisplayName("Discord 알림 전송 테스트 - 미션 부족 알림")
    void sendMissionEmptyAlert_Test() {
        String missionType = "일일미션";
        Long userId = 1L;

        discordNotificationService.sendMissionEmptyAlert(missionType, userId);

        // 알림 전송을 위한 잠시 대기
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Discord 알림 전송 테스트 - log.error 자동 알림")
    void logErrorAlert_Test() {
        log.error("테스트용 ERROR 로그");
        
        try {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다 userId : " + 1L);
        } catch (Exception e) {
            log.error("예외 발생 테스트 - 스택트레이스와 함께 Discord 전송", e);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

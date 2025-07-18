package com.chungnam.eco.common.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

    private final WebClient webClient;

    @Value("${discord.webhook.url}")
    private String discordWebhookUrl;

    @Async("taskExecutor")
    public void sendMissionEmptyAlert(String missionType, Long userId) {
        Map<String, Object> payload = createMissionEmptyPayload(missionType, userId);
        sendPayload(payload);
    }

    private void sendPayload(Map<String, Object> payload) {
        webClient.post()
                .uri(discordWebhookUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class) // 응답 본문이 필요 없으면 Void.class로 처리
                .doOnSuccess(response -> log.info("Discord 알림 전송 성공"))
                .doOnError(error -> log.error("Discord 알림 전송 실패: {}", error.getMessage()))
                .onErrorResume(throwable -> Mono.empty()) // 에러가 발생해도 전체 서비스에 영향을 주지 않음
                .subscribe();
    }

    private Map<String, Object> createMissionEmptyPayload(String missionType, Long userId) {
        List<Map<String, Object>> fields = List.of(
                Map.of("name", "미션 타입", "value", "`" + missionType + "`", "inline", true),
                Map.of("name", "요청 사용자 ID", "value", "`" + userId + "`", "inline", true),
                Map.of("name", "상태", "value", "**활성화된 미션 없음**", "inline", false),
                Map.of("name", "필요 조치", "value", "관리자 패널에서 해당 타입의 미션을 추가해주세요.", "inline", false)
        );

        Map<String, Object> embed = Map.of(
                "title", "⚠️ 미션 고갈 알림",
                "color", 16763904, // 주황색 계열
                "fields", fields,
                "timestamp", Instant.now().toString()
        );

        return Map.of(
                "username", "4Team-Notification-Bot",
                "embeds", List.of(embed)
        );
    }
}

package com.chungnam.eco.common.notification;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ERROR 로그를 Discord로 전송하는 Logback Appender
 * - ERROR 레벨 로그만 Discord로 전송
 * - 비동기 처리로 로깅 성능에 영향을 주지 않음
 * - 가독성 높은 Embed 메시지 포맷 사용
 */
public class DiscordLogAppender extends AppenderBase<ILoggingEvent> {

    @Setter
    private String webhookUrl;

    private WebClient webClient;
    private static final int STACK_TRACE_LIMIT = 3;
    private static final int MESSAGE_LIMIT = 1000; // 필드 값 길이는 1024자 제한

    @Override
    public void start() {
        if (this.webhookUrl == null || this.webhookUrl.trim().isEmpty()) {
            addError("Discord webhook URL이 설정되지 않았습니다. DiscordLogAppender가 비활성화됩니다.");
            return;
        }
        // WebClient 인스턴스 생성
        this.webClient = WebClient.builder().build();
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted() || !event.getLevel().toString().equals("ERROR")) {
            return;
        }

        // 비동기 실행으로 로깅 스레드를 블로킹하지 않음
        CompletableFuture.runAsync(() -> sendToDiscord(event))
                .exceptionally(ex -> {
                    addError("Discord 알림 전송 중 비동기 작업 오류 발생", ex);
                    return null;
                });
    }

    private void sendToDiscord(ILoggingEvent event) {
        Map<String, Object> payload = createDiscordPayload(event);

        this.webClient.post()
                .uri(this.webhookUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> addError("Discord API 호출 실패: " + error.getMessage()))
                .onErrorResume(throwable -> Mono.empty()) // 에러 발생 시 스트림을 종료하지 않고 비움
                .subscribe();
    }

    /**
     * Discord에 보낼 Embed 메시지 페이로드를 생성합니다.
     */
    private Map<String, Object> createDiscordPayload(ILoggingEvent event) {
        List<Map<String, Object>> fields = new ArrayList<>();

        // 1. 에러 메시지 필드
        fields.add(Map.of(
                "name", "📝 Log Message",
                "value", "```" + truncate(event.getFormattedMessage(), MESSAGE_LIMIT) + "```",
                "inline", false
        ));

        // 2. 예외 정보가 있을 경우, 스택 트레이스 필드 추가
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            String stackTrace = buildStackTrace(throwableProxy);
            fields.add(Map.of(
                    "name", "🔍 Stack Trace",
                    "value", "```java\n" + truncate(stackTrace, MESSAGE_LIMIT) + "\n```",
                    "inline", false
            ));
        }

        // 3. 컨텍스트 정보 필드
        fields.add(Map.of(
                "name", "🧵 Thread",
                "value", event.getThreadName(),
                "inline", true
        ));

        fields.add(Map.of(
                "name", "⏰ Time",
                "value", Instant.ofEpochMilli(event.getTimeStamp())
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                "inline", true
        ));

        // 최종 Embed 객체 생성
        Map<String, Object> embed = Map.of(
                "title", "🚨 System Error Report",
                "color", 15158332, // Red
                "fields", fields,
                "timestamp", Instant.ofEpochMilli(event.getTimeStamp()).toString(),
                "footer", Map.of("text", event.getLoggerName())
        );

        return Map.of(
                "username", "4Team-Error-Bot",
                "content", "**에러가 발생했습니다.**",
                "embeds", List.of(embed)
        );
    }

    private String buildStackTrace(IThrowableProxy throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClassName())
                .append(": ")
                .append(throwable.getMessage())
                .append("\n");

        for (int i = 0; i < Math.min(throwable.getStackTraceElementProxyArray().length, STACK_TRACE_LIMIT); i++) {
            sb.append("  at ").append(throwable.getStackTraceElementProxyArray()[i].toString()).append("\n");
        }
        return sb.toString();
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}

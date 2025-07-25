package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.user.controller.response.MissionChoiceResponse;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
import com.chungnam.eco.user.controller.response.MissionSubmitResponse;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIntegrationTest extends TestContainerConfig {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @DisplayName("사용자 메인 정보 조회 - 성공")
    void getUserMainInfo_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");

        // when & then
        webTestClient.get()
                .uri("/api/users/main")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserMainResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getUserInfo()).isNotNull();
                    assertThat(response.getDailyMissions()).isNotNull();
                    assertThat(response.getDailyMissions().size()).isEqualTo(3);
                    assertThat(response.getWeeklyMissions().size()).isEqualTo(1);
                });
    }

    @Test
    @DisplayName("사용자 미션페이지 조회(Mission 선택 완료) - 성공")
    void getMissionPage_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");

        // when & then
        webTestClient.get()
                .uri("/api/missions")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MissionListResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.isDailyMissionSelected()).isTrue();
                    assertThat(response.isWeeklyMissionSelected()).isTrue();
                    assertThat(response.getDailyMissions()).isEmpty();
                    assertThat(response.getWeeklyMissions()).isEmpty();
                    assertThat(response.getUserDailyMissions().size()).isEqualTo(3);
                    assertThat(response.getUserWeeklyMissions().size()).isEqualTo(1);
                });
    }

    @Test
    @DisplayName("사용자 미션상세 조회 - 성공")
    void getMissionDetail_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");

        // when & then
        webTestClient.get()
                .uri("/api/missions/1")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MissionResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(1L);
                    assertThat(response.getTitle()).isEqualTo("플라스틱 분리수거");
                    assertThat(response.getDescription()).isEqualTo("플라스틱 쓰레기를 올바르게 분리수거하세요");
                    assertThat(response.getType()).isEqualTo("DAILY");
                    assertThat(response.getStatus()).isEqualTo("ACTIVATE");
                    assertThat(response.getRewardPoints()).isEqualTo(10);
                });
    }

    @Test
    @DisplayName("미션 제출 - 이미지 없이 제출 시 실패")
    void submitMission_NoImages_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");

        // JSON 부분을 올바른 Content-Type으로 설정
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>("{\"userMissionId\": 1, \"description\": \"미션 완료했습니다!\"}", jsonHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("request", jsonPart);
        body.add("images", "");

        // when & then
        webTestClient.post()
                .uri("/api/missions/submit")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("미션 제출 - 이미지와 함께 제출 시 성공")
    void submitMission_WithImages_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");

        // 실제 이미지 파일 사용
        ClassPathResource imageFile = new ClassPathResource("images/프로필.png");

        ByteArrayResource imageResource;
        try {
            imageResource = new ByteArrayResource(imageFile.getInputStream().readAllBytes()) {
                @Override
                public String getFilename() {
                    return "프로필.png";
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("이미지 파일을 읽을 수 없습니다: " + e.getMessage());
        }

        // JSON 부분을 올바른 Content-Type으로 설정
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>("{\"userMissionId\": 1, \"description\": \"미션 완료했습니다!\"}", jsonHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("request", jsonPart);
        body.add("images", imageResource);

        // when & then
        webTestClient.post()
                .uri("/api/missions/submit")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("미션이 성공적으로 제출되었습니다");
                });
    }

    @Test
    @DisplayName("미션 선택 - 정상적인 요청 (일일 3개, 주간 1개) - 성공")
    void missionChoice_ValidRequest_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER"); // 사용자 2는 미션 선택하지 않은 상태
        String requestBody = """
                {
                    "dailyMissionIds": [1, 2, 3],
                    "weeklyMissionIds": [6]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MissionChoiceResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMessage()).isEqualTo("미션 선택이 완료되었습니다.");
                    assertThat(response.getSelectedDailyMissions()).isEqualTo(3);
                    assertThat(response.getSelectedWeeklyMissions()).isEqualTo(1);
                });
    }

    @Test
    @DisplayName("미션 선택 - 일일 미션 개수 부족 (2개) - 실패")
    void missionChoice_InsufficientDailyMissions_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "dailyMissionIds": [1, 2],
                    "weeklyMissionIds": [6]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("일일 미션은 3개를 선택해야 합니다");
                });
    }

    @Test
    @DisplayName("미션 선택 - 일일 미션 개수 초과 (4개) - 실패")
    void missionChoice_ExcessiveDailyMissions_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "dailyMissionIds": [1, 2, 3, 4],
                    "weeklyMissionIds": [6]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("일일 미션은 3개를 선택해야 합니다");
                });
    }

    @Test
    @DisplayName("미션 선택 - 주간 미션 개수 부족 (0개) - 실패")
    void missionChoice_NoWeeklyMissions_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "dailyMissionIds": [1, 2, 3],
                    "weeklyMissionIds": []
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("주간 미션은 1개를 선택해야 합니다");
                });
    }

    @Test
    @DisplayName("미션 선택 - 주간 미션 개수 초과 (2개) - 실패")
    void missionChoice_ExcessiveWeeklyMissions_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "dailyMissionIds": [1, 2, 3],
                    "weeklyMissionIds": [6, 7]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("주간 미션은 1개를 선택해야 합니다");
                });
    }

    @Test
    @DisplayName("미션 선택 - 존재하지 않는 미션 ID - 실패")
    void missionChoice_NonExistentMissionIds_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "dailyMissionIds": [999, 998, 997],
                    "weeklyMissionIds": [996]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isNotFound() // MissionNotFoundExcption -> 404
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("일부 미션을 찾을 수 없습니다");
                });
    }

    @Test
    @DisplayName("미션 선택 - 필수 필드 누락 (dailyMissionIds null) - 실패")
    void missionChoice_MissingDailyMissionIds_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "weeklyMissionIds": [6]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("일일 미션을 선택해주세요");
                });
    }

    @Test
    @DisplayName("미션 선택 - 필수 필드 누락 (weeklyMissionIds null) - 실패")
    void missionChoice_MissingWeeklyMissionIds_Fail() {
        // given
        String validToken = jwtProvider.generateAccessToken(2L, "USER");
        String requestBody = """
                {
                    "dailyMissionIds": [1, 2, 3]
                }
                """;

        // when & then
        webTestClient.post()
                .uri("/api/missions/choice")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertThat(responseBody).contains("주간 미션을 선택해주세요");
                });
    }
}

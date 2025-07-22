package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.user.controller.response.MissionListResponse;
import com.chungnam.eco.user.controller.response.MissionResponse;
import com.chungnam.eco.user.controller.response.UserMainResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIntegrationTest extends TestContainerConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("사용자 메인 정보 조회 - 성공")
    void getUserMainInfo_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // when
        ResponseEntity<UserMainResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/users/main",
                HttpMethod.GET,
                entity,
                UserMainResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserInfo()).isNotNull();
        assertThat(response.getBody().getDailyMissions()).isNotNull();
        assertThat(response.getBody().getDailyMissions().size()).isEqualTo(3);
        assertThat(response.getBody().getWeeklyMissions().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 미션페이지 조회(Mission 선택 완료) - 성공")
    void getMissionPage_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // when
        ResponseEntity<MissionListResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/missions",
                HttpMethod.GET,
                entity,
                MissionListResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isDailyMissionSelected()).isTrue();
        assertThat(response.getBody().isWeeklyMissionSelected()).isTrue();
        assertThat(response.getBody().getDailyMissions()).isEmpty();
        assertThat(response.getBody().getWeeklyMissions()).isEmpty();
        assertThat(response.getBody().getUserDailyMissions().size()).isEqualTo(3);
        assertThat(response.getBody().getUserWeeklyMissions().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 미션상세 조회 - 성공")
    void getMissionDetail_Success() {
        // given
        String validToken = jwtProvider.generateAccessToken(1L, "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // when
        ResponseEntity<MissionResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/missions/1",
                HttpMethod.GET,
                entity,
                MissionResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getTitle()).isEqualTo("플라스틱 분리수거");
        assertThat(response.getBody().getDescription()).isEqualTo("플라스틱 쓰레기를 올바르게 분리수거하세요");
        assertThat(response.getBody().getType()).isEqualTo("DAILY");
        assertThat(response.getBody().getStatus()).isEqualTo("ACTIVATE");
        assertThat(response.getBody().getRewardPoints()).isEqualTo(10);
    }
}

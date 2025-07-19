package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.config.TestContainerConfig;
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
                "http://localhost:" + port + "/api/members/main",
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
        assertThat(response.getBody().getWeeklyMissions()).isNotNull();
        System.out.println(response.getBody().getWeeklyMissions().toString());
        System.out.println(response.getBody().getDailyMissions().toString());
        System.out.println(response.getBody().toString());
    }
}

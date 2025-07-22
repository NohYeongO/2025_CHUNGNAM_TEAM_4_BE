package com.chungnam.eco.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.chungnam.eco.admin.service.AdminChallengeService;
import com.chungnam.eco.config.TestContainerConfig;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.UserRole;
import com.chungnam.eco.user.repository.UserJPARepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("포인트 차감, 적립 동시성 테스트")
class UserPointServiceTest extends TestContainerConfig {

    @Autowired
    UserJPARepository userRepository;

    @Autowired
    AdminChallengeService adminChallengeService;

    @Test
    @DisplayName("동시성 체크 - 2개의 스레드가 동시에 포인트 적립")
    void concurrencyCheck() throws InterruptedException {
        // given
        User user = User.builder()
                .email("test@email.com")
                .role(UserRole.USER)
                .nickname("test")
                .password("test")
                .point(100)
                .build();

        userRepository.saveAndFlush(user);

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    adminChallengeService.supplyPoint(user.getId(), 100);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertThat(updatedUser.getPoint()).isEqualTo(300); // 100 + 100 + 100
    }
}

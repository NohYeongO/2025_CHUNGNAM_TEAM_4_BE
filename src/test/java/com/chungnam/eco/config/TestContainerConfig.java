package com.chungnam.eco.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class TestContainerConfig {

    protected static MySQLContainer<?> mysql;
    protected static GenericContainer<?> redis;

    static {
        mysql = new MySQLContainer<>("mysql:8.4.5")
                .withDatabaseName("chungnam_thon_test")
                .withUsername("root")
                .withPassword("test");

        redis = new GenericContainer<>("redis:7.2")
                .withExposedPorts(6379)
                .withCommand("redis-server");

        mysql.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL 설정
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysql.getUsername());
        registry.add("spring.datasource.password", () -> mysql.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");

        // Redis 설정
        registry.add("redis.main.host", () -> redis.getHost());
        registry.add("redis.main.port", () -> redis.getMappedPort(6379));
        registry.add("redis.main.password", () -> ""); // 빈 password
        registry.add("redis.main.ssl", () -> "false"); // SSL 비활성화

        // JWT 테스트 설정
        registry.add("jwt.secret", () -> "dGVzdFNlY3JldEtleUZvckpXVFRva2VuVGVzdEVudmlyb25tZW50MTIzNDU2Nzg5MA==");
        registry.add("jwt.expiration", () -> "3600000"); // 1시간

        // Discord 웹훅 활성화 (테스트 환경에서 실제 알림 테스트)
        registry.add("discord.webhook.enabled", () -> "true");
        registry.add("discord.webhook.url", () -> 
            "https://discord.com/api/webhooks/1395237547109781504/iS3I4t2BYbX3hsvnlbX1Js0CeMKZ_vxi8Oqhpq0opPrlqsM1V9jLfqVS-QO_abxIpTde");
    }
} 
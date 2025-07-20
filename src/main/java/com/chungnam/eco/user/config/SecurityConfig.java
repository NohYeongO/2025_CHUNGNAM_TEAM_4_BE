package com.chungnam.eco.user.config;

import com.chungnam.eco.user.jwt.JWTFilter;
import com.chungnam.eco.user.jwt.JWTUtil;
import com.chungnam.eco.user.jwt.LoginFilter; // LoginFilter import 추가
import com.chungnam.eco.user.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthenticationConfiguration authenticationConfiguration;

    // AuthenticationConfiguration 추가 (생성자 수정)
    public SecurityConfig(JWTUtil jwtUtil, TokenService tokenService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    // AuthenticationManager 빈 추가
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF, form login, http basic disable
        http.csrf((auth) -> auth.disable());
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll()
                // .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        // JWT 필터 등록
        http.addFilterBefore(
                new JWTFilter(jwtUtil, tokenService),
                UsernamePasswordAuthenticationFilter.class
        );

        // LoginFilter 필터 체인에 등록 (UsernamePasswordAuthenticationFilter 위치에 추가)
        http.addFilterAt(
                new LoginFilter(authenticationManager(), jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        // 세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

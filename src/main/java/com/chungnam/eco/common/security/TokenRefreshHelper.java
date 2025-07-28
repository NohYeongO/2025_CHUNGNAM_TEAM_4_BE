package com.chungnam.eco.common.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 클라이언트의 토큰 갱신을 도와주는 헬퍼 클래스
 * 
 * 이 클래스는 JWT 필터에서 토큰이 자동 갱신될 때
 * 클라이언트가 새로운 토큰을 인식할 수 있도록 돕습니다.
 */
@Slf4j
@Component
public class TokenRefreshHelper {

    // 새로운 토큰을 전달하기 위한 헤더 이름
    public static final String NEW_ACCESS_TOKEN_HEADER = "X-New-Access-Token";
    public static final String NEW_REFRESH_TOKEN_HEADER = "X-New-Refresh-Token";
    public static final String TOKEN_REFRESHED_HEADER = "X-Token-Refreshed";

    /**
     * 응답에 새로운 토큰 정보를 추가
     * 
     * @param response HTTP 응답 객체
     * @param accessToken 새로운 access token
     * @param refreshToken 새로운 refresh token
     */
    public static void addNewTokensToResponse(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader(NEW_ACCESS_TOKEN_HEADER, accessToken);
        response.setHeader(NEW_REFRESH_TOKEN_HEADER, refreshToken);
        response.setHeader(TOKEN_REFRESHED_HEADER, "true");
        
        log.debug("새로운 토큰이 응답 헤더에 추가되었습니다.");
    }

    /**
     * 클라이언트 개발자를 위한 가이드 정보를 반환
     * 이 메서드는 개발 단계에서 참고용으로 사용할 수 있습니다.
     */
    public static String getClientIntegrationGuide() {
        return """
            
            === 클라이언트 토큰 갱신 처리 가이드 ===
            
            1. Axios 인터셉터 설정 예시 (JavaScript/TypeScript):
            
            ```javascript
            // 응답 인터셉터 설정
            axios.interceptors.response.use(
                (response) => {
                    // 토큰이 자동 갱신된 경우 처리
                    const newAccessToken = response.headers['x-new-access-token'];
                    const newRefreshToken = response.headers['x-new-refresh-token'];
                    const tokenRefreshed = response.headers['x-token-refreshed'];
                    
                    if (tokenRefreshed === 'true' && newAccessToken && newRefreshToken) {
                        // 새로운 토큰들을 저장
                        localStorage.setItem('accessToken', newAccessToken);
                        localStorage.setItem('refreshToken', newRefreshToken);
                        
                        console.log('토큰이 자동으로 갱신되었습니다.');
                    }
                    
                    return response;
                },
                (error) => {
                    // 401 에러이고 리프레시 토큰도 만료된 경우
                    if (error.response?.status === 401) {
                        const errorCode = error.response?.data?.code;
                        
                        if (errorCode === 'A006' || errorCode === 'A007') {
                            // 리프레시 토큰 만료 또는 유효하지 않음
                            // 로그인 페이지로 리다이렉트
                            localStorage.removeItem('accessToken');
                            localStorage.removeItem('refreshToken');
                            window.location.href = '/login';
                        }
                    }
                    
                    return Promise.reject(error);
                }
            );
            ```
            
            2. 요청 인터셉터 설정:
            
            ```javascript
            // 요청 인터셉터에서 토큰 자동 포함
            axios.interceptors.request.use((config) => {
                const token = localStorage.getItem('accessToken');
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
                return config;
            });
            ```
            
            3. 에러 코드별 처리:
            - A006: 리프레시 토큰 만료 → 로그인 페이지로 이동
            - A007: 유효하지 않은 리프레시 토큰 → 로그인 페이지로 이동
            - A008: 토큰 갱신 실패 → 로그인 페이지로 이동
            
            4. 토큰 상태 확인 API 사용:
            
            ```javascript
            // 페이지 로드 시 토큰 유효성 확인
            const checkTokenStatus = async () => {
                try {
                    const response = await axios.get('/api/auth/token/status');
                    if (response.data.valid) {
                        console.log('토큰이 유효합니다.');
                    }
                } catch (error) {
                    // 토큰이 유효하지 않은 경우
                    window.location.href = '/login';
                }
            };
            ```
            
            === 서버 응답 헤더 정보 ===
            - X-New-Access-Token: 갱신된 새로운 access token
            - X-New-Refresh-Token: 갱신된 새로운 refresh token  
            - X-Token-Refreshed: 토큰 갱신 여부 (true/false)
            
            """;
    }
}

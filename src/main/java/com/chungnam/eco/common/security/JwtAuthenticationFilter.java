package com.chungnam.eco.common.security;

import com.chungnam.eco.common.exception.CustomException;
import com.chungnam.eco.common.exception.ErrorCode;
import com.chungnam.eco.common.exception.ErrorResponse;
import com.chungnam.eco.common.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenRefreshService tokenRefreshService;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 인증이 필요없는 경로
    private static final List<String> PERMIT_ALL_PATHS = List.of(
        "/api/auth/signing",
        "/api/auth/signup",
        "/api/auth/refresh",
        "/api/auth/verify",
        "/api/auth/resend-verification",
        "/api/auth/check-email",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    );

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // 인증이 필요없는 경로는 필터 통과
        if (PERMIT_ALL_PATHS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = extractTokenFromRequest(request);
        
        if (token != null && !token.isEmpty()) {
            try {
                // JWT 토큰에서 사용자 ID와 권한 추출
                Long userId = jwtProvider.getUserId(token);
                String userRole = jwtProvider.getUserRole(token);
                
                if (userId != null && userRole != null) {
                    String role = "ROLE_" + userRole;
                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userId, 
                                    null, 
                                    List.of(new SimpleGrantedAuthority(role))
                            );
                    
                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 인증 성공 - User ID: {}, Role: {}", userId, role);
                }
            } catch (ExpiredJwtException e) {
                log.warn("JWT 토큰 만료, 자동 갱신 시도 - Token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
                
                // 만료된 토큰에서 사용자 ID 추출 (만료되어도 페이로드는 읽을 수 있음)
                try {
                    Long userId = Long.valueOf(e.getClaims().getSubject());
                    
                    // Refresh Token을 사용해서 새 토큰 발급 시도
                    TokenRefreshService.TokenRefreshResult refreshResult = tokenRefreshService.refreshAccessToken(userId);
                    
                    // 새로운 토큰으로 인증 설정
                    String role = "ROLE_" + refreshResult.getUserRole();
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userId, 
                                    null, 
                                    List.of(new SimpleGrantedAuthority(role))
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // 응답 헤더에 새로운 토큰들을 포함해서 클라이언트에 전달
                    TokenRefreshHelper.addNewTokensToResponse(response, refreshResult.getAccessToken(), refreshResult.getRefreshToken());
                    
                    log.info("JWT 토큰 자동 갱신 성공 - User ID: {}", userId);
                    
                } catch (CustomException refreshException) {
                    log.warn("JWT 토큰 자동 갱신 실패: {}", refreshException.getMessage());
                    SecurityContextHolder.clearContext();
                    sendErrorResponse(response, refreshException.getErrorCode());
                    return;
                } catch (Exception refreshException) {
                    log.error("JWT 토큰 자동 갱신 중 예상치 못한 오류: {}", refreshException.getMessage());
                    SecurityContextHolder.clearContext();
                    sendErrorResponse(response, ErrorCode.TOKEN_REFRESH_FAILED);
                    return;
                }
                
            } catch (CustomException e) {
                log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, e.getErrorCode());
                return;
            } catch (Exception e) {
                log.warn("JWT 토큰 처리 중 예상치 못한 오류: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * Authorization: Bearer {token} 형태에서 토큰 부분만 추출
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 에러 응답을 JSON 형태로 전송
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(errorCode);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        
        response.getWriter().write(jsonResponse);
    }
}

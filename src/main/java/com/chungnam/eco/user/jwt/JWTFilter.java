package com.chungnam.eco.user.jwt;

import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.UserRole;
import com.chungnam.eco.user.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.PrintWriter;
import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final TokenService tokenService;


    public JWTFilter(JWTUtil jwtUtil, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

// 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("access");

// 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {

            // 만료된건지 확인
            if(jwtUtil.isExpired(accessToken)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.print("Access token expired");
                return;
            }

// 블랙리스트 확인
            if(tokenService.isBlacklisted(accessToken)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.print("Access token blacklisted");
                return;
            }


            String category = jwtUtil.getCategory(accessToken);
            if (!"access".equals(category)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print("Invalid access token");
                return;
            }



            // username, role 값을 획득
            String username = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);

            User user = new User();
            user.setEmail(username);
            user.setRole(UserRole.valueOf(role));
            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.print("Access token expired");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer = response.getWriter();
            writer.print("JWT filter error: " + e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);

    }

}




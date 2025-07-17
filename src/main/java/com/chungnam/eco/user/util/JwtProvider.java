package com.chungnam.eco.user.util;

import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.RefreshToken;
import com.chungnam.eco.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JwtProvider는 JWT 액세스/리프레시 토큰 발급, 검증, 클레임 추출,
 * 그리고 Redis 또는 DB에 리프레시 토큰을 관리하는 유틸리티 컴포넌트입니다.
 */
@Component // 이 클래스를 스프링 빈으로 등록. 주로 의존성 주입해 사용
public class JwtProvider {

    // --- JWT/토큰 관련 설정값 ---

    @Value("${jwt.secret}")
    private String secretKey;                 // 암호화에 사용할 시크릿 키(환경설정으로 주입)
    @Value("${jwt.access.expiration}")
    private long accessTokenValidity;         // 액세스 토큰 만료시간(밀리초)
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidity;        // 리프레시 토큰 만료시간(밀리초)

    private Key key;                          // 실제 서명/검증에 사용할 암호화 키

    // --- 토큰 저장소 관련 컴포넌트 ---

    private final StringRedisTemplate redisTemplate;        // Redis 연동(토큰 저장 시)
    private final RefreshTokenRepository refreshTokenRepo;  // DB 방식 리프레시 토큰 Repository

    /**
     * 생성자에서 Redis와 DB 저장소를 주입받음.
     * @param redisTemplate Redis에 리프레시 토큰 저장에 사용
     * @param refreshTokenRepo DB에 리프레시 토큰 저장에 사용
     */
    @Autowired
    public JwtProvider(StringRedisTemplate redisTemplate, RefreshTokenRepository refreshTokenRepo) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    /**
     * 클래스 초기화 시(secretKey 셋팅 후) 암호화용 Key 객체를 생성.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // === JWT 발급/파싱 메서드 ===

    /**
     * 사용자 정보로 액세스 토큰 발급
     * @param user 토큰에 포함할 User 엔티티
     * @return JWT 액세스 토큰(문자열)
     */
    public String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())                              // 토큰 subject에 유저 email(식별자) 삽입
                .claim("role", user.getRole())                            // 추가 정보: 권한 (role)
                .setIssuedAt(new Date())                                  // 발급 시각
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity)) // 만료 시각
                .signWith(key, SignatureAlgorithm.HS256)                  // 키/알고리즘 지정
                .compact();
    }

    /**
     * 사용자 정보로 리프레시 토큰 발급
     * @param user 토큰에 포함할 User 엔티티
     * @return JWT 리프레시 토큰(문자열)
     */
    public String createRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 이메일만으로 액세스 토큰 발급 (토큰 재발급 등에 사용)
     * @param email 토큰 subject
     * @return JWT 액세스 토큰
     */
    public String createAccessTokenWithEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 이메일만으로 리프레시 토큰 발급 (토큰 재발급 등에 사용)
     * @param email 토큰 subject
     * @return JWT 리프레시 토큰
     */
    public String createRefreshTokenWithEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 이메일(subject)을 추출
     * @param token JWT 문자열
     * @return 토큰에 들어있는 이메일(subject)
     */
    public String getEmail(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 리프레시 토큰의 유효성 검증(만료/파싱 예외 포함)
     * @param token 리프레시 토큰 문자열
     * @return 토큰이 유효하면 true, 만료/잘못된 경우 false
     */
    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 리프레시 토큰이 만료 임박(예: 1일 미만)인지 검사
     * 기한이 임박하면 갱신 권장(API에서 새 토큰 발급 트리거)
     * @param refreshToken 리프레시 토큰 문자열
     * @return 임박(true)/아님(false)
     */
    public boolean isRefreshTokenExpiringSoon(String refreshToken) {
        Date exp = Jwts.parser().setSigningKey(key).build()
                .parseClaimsJws(refreshToken).getBody().getExpiration();
        return exp.getTime() - System.currentTimeMillis() < TimeUnit.DAYS.toMillis(1);
    }

    // === 리프레시 토큰 저장 (Redis 방식) ===

    /**
     * Redis(인메모리 캐시)에 리프레시 토큰 저장
     * @param email 사용자 식별자
     * @param refreshToken 저장할 토큰 문자열
     */
    public void storeRefreshTokenRedis(String email, String refreshToken) {
        try {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set("refresh:" + email, refreshToken, refreshTokenValidity, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Redis에 리프레시 토큰 저장 중 오류 발생", e);
        }
    }

    /**
     * Redis에서 리프레시 토큰 조회 (로그인·토큰검증 등에 활용)
     * @param email 사용자 이메일
     * @return 해당 이메일 리프레시 토큰
     */
    public String getRefreshTokenRedis(String email) {
        try {
            return redisTemplate.opsForValue().get("refresh:" + email);
        } catch (Exception e) {
            throw new RuntimeException("Redis에서 리프레시 토큰 조회 중 오류 발생", e);
        }
    }

    /**
     * Redis에서 리프레시 토큰 삭제(로그아웃 등)
     * @param email 사용자 이메일
     */
    public void deleteRefreshTokenRedis(String email) {
        try {
            redisTemplate.delete("refresh:" + email);
        } catch (Exception e) {
            throw new RuntimeException("Redis에서 리프레시 토큰 삭제 중 오류 발생", e);
        }
    }

    // === 리프레시 토큰 저장 (DB JPA 방식) ===

    /**
     * DB(RefreshToken Entity)에 리프레시 토큰 저장/갱신
     * @param email 사용자 이메일
     * @param refreshToken 저장할 토큰 문자열
     */
    public void storeRefreshTokenDB(String email, String refreshToken) {
        try {
            long expiry = System.currentTimeMillis() + refreshTokenValidity;
            // 이미 토큰이 있으면 갱신, 없으면 새 엔티티 생성
            RefreshToken entity = refreshTokenRepo.findByEmail(email)
                    .map(token -> {
                        token.setToken(refreshToken);
                        token.setExpiry(expiry);
                        return token;
                    })
                    .orElse(new RefreshToken(email, refreshToken, expiry));
            refreshTokenRepo.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("DB에 리프레시 토큰 저장 중 오류 발생", e);
        }
    }

    /**
     * DB에서 리프레시 토큰 조회
     * @param email 사용자 이메일
     * @return 리프레시 토큰(없으면 null)
     */
    public String getRefreshTokenDB(String email) {
        try {
            return refreshTokenRepo.findByEmail(email)
                    .map(RefreshToken::getToken)
                    .orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("DB에서 리프레시 토큰 조회 중 오류 발생", e);
        }
    }

    /**
     * DB에서 리프레시 토큰 삭제 (로그아웃/탈퇴시)
     * @param email 사용자 이메일
     */
    public void deleteRefreshTokenDB(String email) {
        try {
            refreshTokenRepo.deleteByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("DB에서 리프레시 토큰 삭제 중 오류 발생", e);
        }
    }
}

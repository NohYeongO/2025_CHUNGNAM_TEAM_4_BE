package com.chungnam.eco.user.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "verification_token")
public class VerificationToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean used = false;

    @Builder
    public VerificationToken(String token, Long userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.used = false;
    }

    /**
     * 토큰이 만료되었는지 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    /**
     * 토큰이 사용 가능한지 확인 (만료되지 않고 사용되지 않은 상태)
     */
    public boolean isValid() {
        return !isExpired() && !used;
    }

    /**
     * 토큰을 사용 처리
     */
    public void markAsUsed() {
        this.used = true;
    }
}

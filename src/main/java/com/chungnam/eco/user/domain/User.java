package com.chungnam.eco.user.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private Integer point = 0;

    @Builder
    public User(Long id, String email, String password, String nickname, UserRole role, Integer point) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role != null ? role : UserRole.USER;
        this.point = point != null ? point : 0;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    /**
     * 포인트 추가
     */
    public void addPoint(int points) {
        this.point += points;
    }

    /**
     * 포인트 차감
     */
    public void deductPoint(int points) {
        if (this.point < points) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }
        this.point -= points;
    }
}
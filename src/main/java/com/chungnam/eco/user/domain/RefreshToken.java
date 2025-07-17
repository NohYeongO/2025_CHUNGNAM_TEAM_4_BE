package com.chungnam.eco.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RefreshToken 엔티티는 사용자의 리프레시 토큰을 DB에 안전하게 저장하고,
 * 토큰의 만료 및 갱신 관리를 담당하는 JPA 엔티티 클래스입니다.
 */
@Getter // Lombok: 모든 필드에 대한 getter 메서드를 자동으로 생성
@Setter // Lombok: 모든 필드에 대한 setter 메서드를 자동으로 생성
@NoArgsConstructor // Lombok: 기본 생성자(파라미터 없는 생성자) 자동 생성 (JPA 엔티티 필수)
@Entity // JPA: 이 클래스가 DB의 테이블과 매핑됨을 의미
public class RefreshToken {

    /**
     * 기본키(PK) 역할을 하는 id 필드.
     * @Id: JPA에서 엔티티 기본키로 인식
     * @GeneratedValue: 값이 자동 증가됨(즉, DB가 자동 할당)
     * GenerationType.IDENTITY: DB의 auto_increment 방식 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 이메일:
     * 해당 리프레시 토큰이 어떤 사용자에 속한 토큰인지 구별하는 용도.
     * Unique: 하나의 이메일에 하나의 리프레시토큰만 저장 가능
     * Not Null: 반드시 값이 존재해야 함
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 실제 리프레시 토큰 값(String)
     * 최대 512자까지 허용, Not Null: 반드시 있어야 함
     */
    @Column(nullable = false, length = 512)
    private String token;

    /**
     * 리프레시 토큰의 만료 시각(타임스탬프, 밀리초)
     * Not Null: 만료 정보는 항상 존재해야함
     * 보통 System.currentTimeMillis()+유효기간(millis)로 계산하여 저장
     */
    @Column(nullable = false)
    private Long expiry;

    /**
     * 이 필드는 JPA, Jackson 등에 의해 자동 사용되는 기본 생성자 외에
     * 실제 객체 생성 시 값 할당용으로 사용하는 커스텀 생성자
     * @param email  사용자 이메일
     * @param token  발급된 리프레시 토큰 문자열
     * @param expiry 만료 시각(Unix epoch millis)
     */
    public RefreshToken(String email, String token, Long expiry) {
        this.email = email;
        this.token = token;
        this.expiry = expiry;
    }
}

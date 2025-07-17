package com.chungnam.eco.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Getter                     // Lombok: 모든 필드에 대해 getter 메서드 자동 생성 (코드 간결화, 데이터 읽기 전용 시 사용)
@Setter                     // Lombok: 모든 필드에 대해 setter 메서드 자동 생성 (필드 값 외부에서 쉽게 설정)
@NoArgsConstructor          // Lombok: 파라미터 없는 기본 생성자 자동 생성 (JPA에서 필수, 객체 직렬화/역직렬화 등에 필요)
@Entity                     // JPA: 이 클래스가 "DB 테이블에 매핑되는 엔티티 클래스"임을 선언
@Table(name = "user")       // JPA: 실제 DB에서 매핑될 테이블명을 "user"로 지정

public class User {

    /**
     * 사용자 고유 아이디(Primary Key). 각 회원을 DB에서 유일하게 식별함.
     * @Id: 기본키 필드
     * @GeneratedValue: 값을 DB가 자동으로 생성 (IDENTITY: auto_increment)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 이메일(로그인 ID와 동일)
     * unique: DB에서 중복 불가
     * length: 최대 255자 제한
     * nullable=false: 값 필수
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * 회원 비밀번호(암호화 저장)
     * nullable=false: 필수 입력
     * length=255: 충분히 긴 해시값 저장 가능
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 회원 이름
     * unique=true: 실명 중복 방지(정책에 따라 변경 가능)
     * nullable=false: 필수 입력
     * length=50: 이름 최대 길이 제한
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 회원 권한(예: "USER", "ADMIN")
     * 관리 기능/권한 분리에 사용
     * length=20: 역할명 짧게 제한
     */
    @Column(nullable = false, length = 20)
    private String role;

    /**
     * 회원 포인트(정수, 기본값 0)
     * 서비스 내 경제적 보상/점수 등에 사용
     * nullable=false: 0으로 초기화
     */
    @Column(nullable = false)
    private Integer point = 0;

    /**
     * 회원 생성 일시 (DB 컬럼명: created_at)
     * 회원 가입한 시간 기록
     * nullable=false: 항상 값 필요
     */
    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

    /**
     * 회원 정보 마지막 수정 일시 (DB 컬럼명: updated_at)
     * 정보 변경·이력 관리 용도
     * nullable=false: 항상 값 필요
     */
    @Column(nullable = false, name = "updated_at")
    private Timestamp updatedAt;
}

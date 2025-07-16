package com.chungnam.eco.challenge.domain;

import com.chungnam.eco.challenge.exception.InvalidChallengeException;
import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "challenge_image")
public class ChallengeImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Lob
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "stored_name", nullable = false, unique = true)
    private String storedName;

    @Column(nullable = false, length = 1)
    private Integer sort;

    @Lob
    @Column(nullable = false)
    private String url;


    @Builder
    public ChallengeImage(Challenge challenge, String originalName, Integer sort) {
        this.challenge = challenge;
        this.originalName = originalName;
        this.sort = sort != null ? sort : 1; // null-safe
        this.storedName = generateStoredName();
        this.url = generateUrl();
    }

    // UUID 자동생성
    private String generateStoredName() {
        return java.util.UUID.randomUUID().toString();
    }

    // 이미지 경로 생성
    private String generateUrl() {
        if (this.challenge == null || this.challenge.getId() == null) {
            throw new InvalidChallengeException(ErrorCode.INVALID_CHALLENGE); // challenge 없을 경우 예외 처리
        }
        return "/images/" + this.challenge.getId() + "/" + this.storedName; // challenge id 로 폴더 생성 후 이미지 저장
    }

}

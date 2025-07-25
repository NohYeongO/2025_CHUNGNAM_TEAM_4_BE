package com.chungnam.eco.challenge.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.common.exception.InvalidChallengeException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
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
    public ChallengeImage(Challenge challenge, String originalName, String storedName, Integer sort, String url) {
        this.challenge = challenge;
        this.originalName = originalName;
        this.sort = sort;
        this.storedName = storedName;
        this.url = url;
    }
}

package com.chungnam.eco.community.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "post_images")
public class PostImages extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "stored_name", nullable = false, unique = true, length = 255)
    private String storedName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder
    public PostImages(Long id, Posts post, String originalName, String storedName, String fileUrl, Integer sortOrder) {
        this.id = id;
        this.post = post;
        this.originalName = originalName;
        this.storedName = storedName;
        this.fileUrl = fileUrl;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }

    /**
     * 게시글 연관관계 설정
     */
    public void setPost(Posts post) {
        this.post = post;
        if (post != null && !post.getImages().contains(this)) {
            post.getImages().add(this);
        }
    }
}

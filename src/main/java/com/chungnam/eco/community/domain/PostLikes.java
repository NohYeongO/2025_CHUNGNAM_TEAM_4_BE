package com.chungnam.eco.community.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.user.domain.User;
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
@Table(name = "post_likes", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "member_id"})})
public class PostLikes extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Builder
    public PostLikes(Long id, Posts post, User member) {
        this.id = id;
        this.post = post;
        this.member = member;
    }

    /**
     * 게시글 연관관계 설정
     */
    public void setPost(Posts post) {
        this.post = post;
        if (post != null && !post.getLikes().contains(this)) {
            post.getLikes().add(this);
        }
    }
}

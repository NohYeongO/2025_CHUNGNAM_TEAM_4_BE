package com.chungnam.eco.community.controller.response;

import com.chungnam.eco.community.domain.Posts;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String memberNickname;
    private final Integer likeCount;
    private final Integer commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<PostImageResponse> images;
    private final boolean isLiked;

    public static PostDetailResponse from(Posts post, boolean isLiked) {
        List<PostImageResponse> imageResponses = post.getImages().stream()
                .map(PostImageResponse::from)
                .toList();

        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberNickname(post.getMember().getNickname())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .images(imageResponses)
                .isLiked(isLiked)
                .build();
    }
}

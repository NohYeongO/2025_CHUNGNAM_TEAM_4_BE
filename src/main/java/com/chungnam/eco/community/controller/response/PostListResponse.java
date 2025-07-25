package com.chungnam.eco.community.controller.response;

import com.chungnam.eco.community.domain.Posts;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostListResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String memberNickname;
    private final Integer likeCount;
    private final Integer commentCount;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;

    public static PostListResponse from(Posts post) {
        List<String> imageUrls = post.getImages().stream()
                .map(image -> image.getFileUrl())
                .toList();

        return PostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent().length() > 100 ? 
                        post.getContent().substring(0, 100) + "..." : 
                        post.getContent())
                .memberNickname(post.getMember().getNickname())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .imageUrls(imageUrls)
                .build();
    }
}

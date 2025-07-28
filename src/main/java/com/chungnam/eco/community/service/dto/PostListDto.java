package com.chungnam.eco.community.service.dto;

import com.chungnam.eco.community.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostListDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String userNickname;
    private final Integer likeCount;
    private final Integer commentCount;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;

    public static PostListDto from(Post post, List<String> imageUrls) {
        return PostListDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userNickname(post.getUser().getNickname())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .imageUrls(imageUrls != null ? imageUrls : List.of())
                .build();
    }
}

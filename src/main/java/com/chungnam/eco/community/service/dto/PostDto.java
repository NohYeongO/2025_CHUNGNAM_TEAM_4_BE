package com.chungnam.eco.community.service.dto;

import com.chungnam.eco.community.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDto {
    private final Long postId;
    private final Long userId;
    private final String title;
    private final String content;

    public static PostDto from(Post post, Long userId) {
        return PostDto.builder()
                .postId(post.getId())
                .userId(userId)
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}

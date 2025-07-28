package com.chungnam.eco.community.service.dto;

import com.chungnam.eco.community.domain.Post;
import com.chungnam.eco.community.domain.PostImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String userNickname;
    private final Integer likeCount;
    private final Integer commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<PostImageDto> images;
    private final boolean isLiked;

    public static PostDetailDto from(Post post, List<PostImage> images, boolean isLiked, String sasToken) {
        List<PostImageDto> imageList = images.stream()
                .map(image -> {
                    PostImageDto dto = PostImageDto.from(image);
                    String urlWithToken = dto.getUrl() + (sasToken != null ? sasToken : "");
                    return PostImageDto.builder()
                            .id(dto.getId())
                            .url(urlWithToken)
                            .build();
                })
                .toList();

        return PostDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userNickname(post.getUser().getNickname())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .images(imageList)
                .isLiked(isLiked)
                .build();
    }
}

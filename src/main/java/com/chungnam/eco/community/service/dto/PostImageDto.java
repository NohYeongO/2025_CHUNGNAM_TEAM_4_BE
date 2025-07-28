package com.chungnam.eco.community.service.dto;

import com.chungnam.eco.community.domain.PostImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostImageDto {
    private final Long id;
    private final String originalName;
    private final String url;
    private final Integer sort;

    public static PostImageDto from(PostImage postImage) {
        return PostImageDto.builder()
                .id(postImage.getId())
                .originalName(postImage.getOriginalName())
                .url(postImage.getUrl())
                .sort(postImage.getSort())
                .build();
    }
}

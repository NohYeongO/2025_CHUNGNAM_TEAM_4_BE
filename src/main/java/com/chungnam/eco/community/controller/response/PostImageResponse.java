package com.chungnam.eco.community.controller.response;

import com.chungnam.eco.community.domain.PostImages;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostImageResponse {

    private final Long id;
    private final String originalName;
    private final String fileUrl;
    private final Integer sortOrder;

    public static PostImageResponse from(PostImages postImage) {
        return PostImageResponse.builder()
                .id(postImage.getId())
                .originalName(postImage.getOriginalName())
                .fileUrl(postImage.getFileUrl())
                .sortOrder(postImage.getSortOrder())
                .build();
    }
}

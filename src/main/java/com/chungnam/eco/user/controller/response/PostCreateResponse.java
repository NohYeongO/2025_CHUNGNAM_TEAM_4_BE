package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateResponse {

    private final boolean success;
    private final String message;
    private final Long postId;

    /**
     * 게시글 작성 성공
     */
    public static PostCreateResponse success(Long postId) {
        return PostCreateResponse.builder()
                .success(true)
                .message("게시글이 성공적으로 작성되었습니다.")
                .postId(postId)
                .build();
    }

    /**
     * 게시글 작성 성공 (커스텀 메시지)
     */
    public static PostCreateResponse success(Long postId, String message) {
        return PostCreateResponse.builder()
                .success(true)
                .message(message)
                .postId(postId)
                .build();
    }
}

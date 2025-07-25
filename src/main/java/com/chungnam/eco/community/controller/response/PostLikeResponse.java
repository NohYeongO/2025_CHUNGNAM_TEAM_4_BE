package com.chungnam.eco.community.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostLikeResponse {

    private final boolean success;
    private final String message;
    private final boolean isLiked;
    private final Integer likeCount;

    /**
     * 좋아요 추가 성공
     */
    public static PostLikeResponse liked(Integer likeCount) {
        return PostLikeResponse.builder()
                .success(true)
                .message("좋아요가 추가되었습니다.")
                .isLiked(true)
                .likeCount(likeCount)
                .build();
    }

    /**
     * 좋아요 취소 성공
     */
    public static PostLikeResponse unliked(Integer likeCount) {
        return PostLikeResponse.builder()
                .success(true)
                .message("좋아요가 취소되었습니다.")
                .isLiked(false)
                .likeCount(likeCount)
                .build();
    }

    /**
     * 좋아요 처리 실패
     */
    public static PostLikeResponse failure(String message) {
        return PostLikeResponse.builder()
                .success(false)
                .message(message)
                .isLiked(false)
                .likeCount(null)
                .build();
    }
}

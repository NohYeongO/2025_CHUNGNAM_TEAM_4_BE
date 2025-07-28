package com.chungnam.eco.user.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.community.service.dto.PostDetailDto;
import com.chungnam.eco.user.controller.response.PostCreateResponse;
import com.chungnam.eco.user.controller.response.PostLikeResponse;
import com.chungnam.eco.user.controller.response.PostPageResponse;
import com.chungnam.eco.user.service.UserAppService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users/community")
@RequiredArgsConstructor
public class CommunityController {

    private final UserAppService userAppService;

    /**
     * 게시글 목록 조회 API
     * @param page 페이지 번호 (기본값: 0)
     * @param limit 페이지 크기 (기본값: 10)
     * @param sort 정렬 방식 (date: 날짜순, like: 추천순, 기본값: date)
     * @return 게시글 목록 (페이징 정보 포함)
     */
    @GetMapping("/posts")
    public ResponseEntity<PostPageResponse> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sort) {
        return ResponseEntity.ok(PostPageResponse.from(userAppService.getPostList(page, limit, sort)));
    }

    /**
     * 게시글 상세 조회 API
     * @param postId 게시글 ID
     * @return 게시글 상세 정보
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(@PathVariable Long postId) {
        Long currentUserId = AuthenticationHelper.getCurrentUserId();
        PostDetailDto postDetail = userAppService.getPostDetail(postId, currentUserId);
        return ResponseEntity.ok(postDetail);
    }

    /**
     * 게시글 작성 API (인증 필요) - 이미지 포함
     * @param title 게시글 제목
     * @param content 게시글 내용  
     * @param images 첨부할 이미지 파일들 (선택사항, 최대 3장)
     * @return 게시글 작성 결과
     */
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostCreateResponse> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart("images") @NotNull(message = "이미지는 필수입니다.")
            @Size(min = 1, max = 3, message = "이미지는 1~3장까지 업로드 가능합니다.") List<MultipartFile> images) {
        Long userId = AuthenticationHelper.getCurrentUserId();
        Long postId = userAppService.createPost(title, content, images, userId);
        return ResponseEntity.ok(PostCreateResponse.success(postId));
    }

    /**
     * 게시글 좋아요 토글 API (인증 필요)
     * @param postId 게시글 ID
     * @return 좋아요 처리 결과
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> togglePostLike(@PathVariable Long postId) {
        Long userId = AuthenticationHelper.getCurrentUserId();
        boolean isLiked = userAppService.togglePostLike(postId, userId);
        return ResponseEntity.ok(PostLikeResponse.from(isLiked));
    }
}

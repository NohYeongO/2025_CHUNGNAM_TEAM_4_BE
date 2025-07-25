package com.chungnam.eco.community.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.community.controller.request.PostCreateRequest;
import com.chungnam.eco.community.controller.response.PostCreateResponse;
import com.chungnam.eco.community.controller.response.PostDetailResponse;
import com.chungnam.eco.community.controller.response.PostListResponse;
import com.chungnam.eco.community.controller.response.PostLikeResponse;
import com.chungnam.eco.community.service.CommunityImageService;
import com.chungnam.eco.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityImageService communityImageService;

    /**
     * 게시글 목록 조회 API
     * @param page 페이지 번호 (기본값: 0)
     * @param limit 페이지 크기 (기본값: 10)
     * @return 게시글 목록 (페이징 정보 포함)
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<PostListResponse>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit);
        Page<PostListResponse> postList = communityService.getPostList(pageable);
        return ResponseEntity.ok(postList);
    }

    /**
     * 게시글 상세 조회 API
     * @param postId 게시글 ID
     * @return 게시글 상세 정보
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        Long currentUserId = null;
        try {
            currentUserId = AuthenticationHelper.getCurrentUserId();
        } catch (Exception e) {
            // 비로그인 사용자의 경우 currentUserId는 null로 유지
        }
        
        PostDetailResponse postDetail = communityService.getPostDetail(postId, currentUserId);
        return ResponseEntity.ok(postDetail);
    }

    /**
     * 게시글 작성 API (인증 필요)
     * @param request 게시글 작성 요청 정보
     * @return 게시글 작성 결과
     */
    @PostMapping("/posts")
    public ResponseEntity<PostCreateResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        Long memberId = AuthenticationHelper.getCurrentUserId();
        PostCreateResponse response = communityService.createPost(request, memberId);
        
        return response.isSuccess() 
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * 게시글 좋아요 토글 API (인증 필요)
     * @param postId 게시글 ID
     * @return 좋아요 처리 결과
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> togglePostLike(@PathVariable Long postId) {
        Long memberId = AuthenticationHelper.getCurrentUserId();
        PostLikeResponse response = communityService.togglePostLike(postId, memberId);
        
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * 이미지 업로드 API (인증 필요)
     * @param files 업로드할 이미지 파일들
     * @return 업로드된 이미지 URL 목록
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> imageUrls = communityImageService.uploadImages(files);
            return ResponseEntity.ok().body(new ImageUploadResponse(true, "이미지 업로드 성공", imageUrls));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ImageUploadResponse(false, e.getMessage(), null));
        }
    }

    /**
     * 단일 이미지 업로드 API (인증 필요)
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지 URL
     */
    @PostMapping("/upload/single")
    public ResponseEntity<?> uploadSingleImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = communityImageService.uploadImage(file);
            return ResponseEntity.ok().body(new SingleImageUploadResponse(true, "이미지 업로드 성공", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new SingleImageUploadResponse(false, e.getMessage(), null));
        }
    }

    // 내부 클래스로 응답 DTO 정의
    private static class ImageUploadResponse {
        public final boolean success;
        public final String message;
        public final List<String> imageUrls;

        public ImageUploadResponse(boolean success, String message, List<String> imageUrls) {
            this.success = success;
            this.message = message;
            this.imageUrls = imageUrls;
        }
    }

    private static class SingleImageUploadResponse {
        public final boolean success;
        public final String message;
        public final String imageUrl;

        public SingleImageUploadResponse(boolean success, String message, String imageUrl) {
            this.success = success;
            this.message = message;
            this.imageUrl = imageUrl;
        }
    }
}

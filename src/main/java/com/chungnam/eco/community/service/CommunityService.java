package com.chungnam.eco.community.service;

import com.chungnam.eco.common.exception.PostNotFoundException;
import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.community.controller.request.PostCreateRequest;
import com.chungnam.eco.community.controller.response.PostCreateResponse;
import com.chungnam.eco.community.controller.response.PostDetailResponse;
import com.chungnam.eco.community.controller.response.PostListResponse;
import com.chungnam.eco.community.controller.response.PostLikeResponse;
import com.chungnam.eco.community.domain.PostImages;
import com.chungnam.eco.community.domain.PostLikes;
import com.chungnam.eco.community.domain.Posts;
import com.chungnam.eco.community.repository.PostImagesJPARepository;
import com.chungnam.eco.community.repository.PostLikesJPARepository;
import com.chungnam.eco.community.repository.PostsJPARepository;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostsJPARepository postsRepository;
    private final PostImagesJPARepository postImagesRepository;
    private final PostLikesJPARepository postLikesRepository;
    private final UserJPARepository userRepository;

    /**
     * 게시글 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<PostListResponse> getPostList(Pageable pageable) {
        Page<Posts> posts = postsRepository.findAllByOrderByCreatedAtDesc(pageable);
        return posts.map(PostListResponse::from);
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long currentUserId) {
        Posts post = postsRepository.findByIdWithMember(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

        boolean isLiked = currentUserId != null && 
                postLikesRepository.existsByPostIdAndMemberId(postId, currentUserId);

        return PostDetailResponse.from(post, isLiked);
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request, Long memberId) {
        try {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + memberId));

            Posts post = Posts.builder()
                    .member(member)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .build();

            Posts savedPost = postsRepository.save(post);

            // 이미지가 있다면 저장
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                savePostImages(savedPost, request.getImageUrls());
            }

            return PostCreateResponse.success(savedPost.getId());
        } catch (Exception e) {
            return PostCreateResponse.failure("게시글 작성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 게시글 좋아요 토글
     */
    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long memberId) {
        try {
            Posts post = postsRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + memberId));

            boolean isLiked = postLikesRepository.existsByPostIdAndMemberId(postId, memberId);

            if (isLiked) {
                // 좋아요 취소
                postLikesRepository.deleteByPostIdAndMemberId(postId, memberId);
                post.decreaseLikeCount();
                return PostLikeResponse.unliked(post.getLikeCount());
            } else {
                // 좋아요 추가
                PostLikes postLike = PostLikes.builder()
                        .post(post)
                        .member(member)
                        .build();
                postLikesRepository.save(postLike);
                post.increaseLikeCount();
                return PostLikeResponse.liked(post.getLikeCount());
            }
        } catch (Exception e) {
            return PostLikeResponse.failure("좋아요 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 게시글 이미지 저장
     */
    private void savePostImages(Posts post, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            PostImages postImage = PostImages.builder()
                    .post(post)
                    .originalName(extractFileNameFromUrl(imageUrl))
                    .storedName(generateStoredName())
                    .fileUrl(imageUrl)
                    .sortOrder(i)
                    .build();
            
            postImagesRepository.save(postImage);
        }
    }

    /**
     * URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    /**
     * 저장될 파일명 생성
     */
    private String generateStoredName() {
        return UUID.randomUUID().toString();
    }
}

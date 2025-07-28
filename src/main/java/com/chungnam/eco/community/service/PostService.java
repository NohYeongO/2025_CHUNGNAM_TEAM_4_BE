package com.chungnam.eco.community.service;

import com.chungnam.eco.common.exception.PostNotFoundException;
import com.chungnam.eco.common.exception.PostLikeException;
import com.chungnam.eco.community.domain.Post;
import com.chungnam.eco.community.domain.PostImage;
import com.chungnam.eco.community.domain.PostLike;
import com.chungnam.eco.community.repository.PostJPARepository;
import com.chungnam.eco.community.repository.PostImageJPARepository;
import com.chungnam.eco.community.repository.PostLikeJPARepository;
import com.chungnam.eco.community.service.dto.PostDetailDto;
import com.chungnam.eco.community.service.dto.PostListDto;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostJPARepository postRepository;
    private final PostImageJPARepository postImageRepository;
    private final PostLikeJPARepository postLikeRepository;

    /**
     * 게시글 목록 조회 (페이징 및 정렬)
     */
    @Transactional(readOnly = true)
    public Page<PostListDto> getPostList(int page, int limit, String sort) {
        Sort sortOrder = switch (sort.toLowerCase()) {
            case "like" -> Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt"));
            default -> Sort.by(Sort.Order.desc("createdAt"));
        };

        Pageable pageable = PageRequest.of(page, limit, sortOrder);
        Page<Post> posts = postRepository.findAllActivePosts(pageable);

        return posts.map(post -> {
            List<String> imageUrls = postImageRepository.findByPostIdOrderBySort(post.getId())
                    .stream()
                    .map(PostImage::getUrl)
                    .toList();
            return PostListDto.from(post, imageUrls);
        });
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postId, UserInfoDto userInfo) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));
        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userInfo.getUserId());
        List<PostImage> images = postImageRepository.findByPostIdOrderBySort(postId);
        return PostDetailDto.from(post, images, isLiked);
    }

    /**
     * 게시글 좋아요 토글
     */
    @Transactional
    public boolean togglePostLike(Long postId, UserInfoDto userInfo) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

            boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userInfo.getUserId());

            if (isLiked) {
                // 좋아요 취소
                postLikeRepository.deleteByPostIdAndUserId(postId, userInfo.getUserId());
                post.decreaseLikeCount();
                return false;
            } else {
                // 좋아요 추가
                PostLike postLike = PostLike.builder()
                        .post(post)
                        .user(userInfo.toEntity())
                        .build();
                postLikeRepository.save(postLike);
                post.increaseLikeCount();
                return true;
            }
        } catch (PostNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PostLikeException("좋아요 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

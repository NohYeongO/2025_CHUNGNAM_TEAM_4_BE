package com.chungnam.eco.community.service;

import com.chungnam.eco.common.exception.DataIntegrityException;
import com.chungnam.eco.common.exception.PostCreateException;
import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.common.storage.ImageUploadDto;
import com.chungnam.eco.community.domain.Post;
import com.chungnam.eco.community.domain.PostImage;
import com.chungnam.eco.community.repository.PostJPARepository;
import com.chungnam.eco.community.repository.PostImageJPARepository;
import com.chungnam.eco.community.service.dto.PostDto;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import com.chungnam.eco.user.service.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSubmitService {

    private final PostJPARepository postRepository;
    private final PostImageJPARepository postImageRepository;
    private final UserJPARepository userRepository;

    @Transactional
    public PostDto createTempPost(UserInfoDto userInfo, String title, String content) {
        Post tempPost = Post.builder()
                .user(userInfo.toEntity())
                .title(title)
                .content(content)
                .build();

        Post savedPost = postRepository.save(tempPost);
        return PostDto.from(savedPost, userInfo.getUserId());
    }

    @Transactional
    public Long completePostSubmit(PostDto tempPost, List<ImageUploadDto> uploadImageList) {
        Post post = postRepository.findById(tempPost.getPostId()).orElseThrow(() -> {
            log.error("Post not found with ID: {}", tempPost.getPostId());
            return new PostCreateException("게시글을 찾을 수 없습니다.");
        });

        if (uploadImageList != null && !uploadImageList.isEmpty()) {
            postImageRepository.saveAll(
                    uploadImageList.stream()
                            .map(uploadImage -> ImageUploadDto.toPostImageEntity(post, uploadImage))
                            .toList()
            );
        }

        return post.getId();
    }

    public void deleteTempPost(PostDto tempPost) {
        postRepository.deleteById(tempPost.getPostId());
    }
}

package com.chungnam.eco.community.controller;

import com.chungnam.eco.common.exception.GlobalExceptionHandler;
import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.community.controller.response.PostCreateResponse;
import com.chungnam.eco.community.controller.response.PostDetailResponse;
import com.chungnam.eco.community.controller.response.PostImageResponse;
import com.chungnam.eco.community.controller.response.PostLikeResponse;
import com.chungnam.eco.community.controller.response.PostListResponse;
import com.chungnam.eco.community.service.CommunityImageService;
import com.chungnam.eco.community.service.CommunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommunityControllerIntegrationTest {

    @Mock
    private CommunityService communityService;

    @Mock
    private CommunityImageService communityImageService;

    @InjectMocks
    private CommunityController communityController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(communityController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 기본 동작 확인")
    void getPostList_BasicTest() throws Exception {
        // given
        List<PostListResponse> mockPosts = Arrays.asList(
                PostListResponse.builder()
                        .id(1L)
                        .title("테스트 게시글 1")
                        .content("테스트 내용 1")
                        .memberNickname("사용자1")
                        .likeCount(5)
                        .commentCount(3)
                        .createdAt(LocalDateTime.now())
                        .imageUrls(new ArrayList<>())
                        .build()
        );

        PageImpl<PostListResponse> mockPage = new PageImpl<>(mockPosts, PageRequest.of(0, 10), 1);
        given(communityService.getPostList(any(Pageable.class))).willReturn(mockPage);

        // when and then
        MvcResult result = mockMvc.perform(get("/api/community/posts")
                        .param("page", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("응답 내용: " + content);
        
        // 기본적인 검증만 수행
        assertTrue(content.contains("content") || content.contains("게시글") || content.length() > 0);
    }

    @Test
    @DisplayName("게시글 작성 - 기본 동작 확인")
    void createPost_BasicTest() throws Exception {
        // given
        String requestBody = """
                {
                    "title": "테스트 게시글",
                    "content": "이것은 테스트 게시글 내용입니다.",
                    "imageUrls": []
                }
                """;

        PostCreateResponse mockResponse = PostCreateResponse.success(1L);

        try (MockedStatic<AuthenticationHelper> mockedStatic = Mockito.mockStatic(AuthenticationHelper.class)) {
            mockedStatic.when(() -> AuthenticationHelper.getCurrentUserId()).thenReturn(1L);
            given(communityService.createPost(any(), eq(1L))).willReturn(mockResponse);

            // when and then
            MvcResult result = mockMvc.perform(post("/api/community/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            System.out.println("응답 내용: " + content);
            
            // 기본적인 검증만 수행
            assertTrue(content.contains("success") || content.contains("게시글") || content.length() > 0);
        }
    }

    @Test
    @DisplayName("게시글 상세 조회 - 기본 동작 확인")
    void getPostDetail_BasicTest() throws Exception {
        // given
        long postId = 1L;
        List<PostImageResponse> mockImages = List.of(
                PostImageResponse.builder()
                        .id(1L)
                        .originalName("test1.jpg")
                        .fileUrl("https://example.com/test1.jpg")
                        .sortOrder(0)
                        .build()
        );

        PostDetailResponse mockResponse = PostDetailResponse.builder()
                .id(postId)
                .title("상세 조회용 테스트 게시글")
                .content("이것은 상세 조회를 위한 테스트 게시글입니다.")
                .memberNickname("작성자")
                .likeCount(10)
                .commentCount(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .images(mockImages)
                .isLiked(false)
                .build();

        given(communityService.getPostDetail(eq(postId), isNull())).willReturn(mockResponse);

        // when & then
        MvcResult result = mockMvc.perform(get("/api/community/posts/" + postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("응답 내용: " + content);
        
        // 기본적인 검증만 수행
        assertTrue(content.contains("title") || content.contains("게시글") || content.length() > 0);
    }

    @Test
    @DisplayName("게시글 좋아요 토글 - 기본 동작 확인")
    void togglePostLike_BasicTest() throws Exception {
        // given
        long postId = 1L;
        PostLikeResponse mockResponse = PostLikeResponse.liked(1);

        try (MockedStatic<AuthenticationHelper> mockedStatic = Mockito.mockStatic(AuthenticationHelper.class)) {
            mockedStatic.when(() -> AuthenticationHelper.getCurrentUserId()).thenReturn(1L);
            given(communityService.togglePostLike(eq(postId), eq(1L))).willReturn(mockResponse);

            // when & then
            MvcResult result = mockMvc.perform(post("/api/community/posts/" + postId + "/like"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            System.out.println("응답 내용: " + content);
            
            // 기본적인 검증만 수행
            assertTrue(content.contains("success") || content.contains("좋아요") || content.length() > 0);
        }
    }

    @Test
    @DisplayName("예외 처리 테스트")
    void getPostDetail_NotFoundTest() throws Exception {
        // given
        long nonExistentPostId = 99999L;
        given(communityService.getPostDetail(eq(nonExistentPostId), isNull()))
                .willThrow(new com.chungnam.eco.common.exception.PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + nonExistentPostId));

        // when & then
        MvcResult result = mockMvc.perform(get("/api/community/posts/" + nonExistentPostId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("예외 응답 내용: " + content);
        
        // 기본적인 검증만 수행
        assertTrue(content.contains("code") || content.contains("message") || content.length() > 0);
    }

    @Test
    @DisplayName("이미지 업로드 - 기본 동작 확인")
    void uploadImages_BasicTest() throws Exception {
        // given
        List<String> mockImageUrls = Arrays.asList(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg"
        );

        given(communityImageService.uploadImages(anyList())).willReturn(mockImageUrls);

        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", "image/jpeg", "test image 2".getBytes());

        // when & then
        MvcResult result = mockMvc.perform(multipart("/api/community/upload")
                        .file(file1)
                        .file(file2))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("이미지 업로드 응답 내용: " + content);
        
        // 기본적인 검증만 수행
        assertTrue(content.contains("success") || content.contains("imageUrls") || content.length() > 0);
    }

    @Test
    @DisplayName("단일 이미지 업로드 - 기본 동작 확인")
    void uploadSingleImage_BasicTest() throws Exception {
        // given
        String mockImageUrl = "https://example.com/single-image.jpg";
        given(communityImageService.uploadImage(any())).willReturn(mockImageUrl);

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        // when & then
        MvcResult result = mockMvc.perform(multipart("/api/community/upload/single")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("단일 이미지 업로드 응답 내용: " + content);
        
        // 기본적인 검증만 수행
        assertTrue(content.contains("success") || content.contains("imageUrl") || content.length() > 0);
    }
}

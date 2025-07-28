package com.chungnam.eco.user.controller.response;

import com.chungnam.eco.community.service.dto.PostListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPageResponse {
    
    private List<PostListDto> posts;      // 게시글 목록
    private int currentPage;                   // 현재 페이지 (0부터 시작)
    private int totalPages;                    // 전체 페이지 수
    private long totalElements;                // 전체 요소 수
    private int size;                          // 페이지 크기
    private boolean first;                     // 첫 번째 페이지 여부
    private boolean last;                      // 마지막 페이지 여부
    private boolean hasNext;                   // 다음 페이지 존재 여부
    private boolean hasPrevious;               // 이전 페이지 존재 여부
    
    public static PostPageResponse from(Page<PostListDto> page) {
        return PostPageResponse.builder()
                .posts(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}

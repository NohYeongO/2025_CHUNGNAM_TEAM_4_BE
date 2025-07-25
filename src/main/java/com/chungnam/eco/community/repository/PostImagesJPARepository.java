package com.chungnam.eco.community.repository;

import com.chungnam.eco.community.domain.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImagesJPARepository extends JpaRepository<PostImages, Long> {

    /**
     * 특정 게시글의 이미지 목록 조회 (정렬 순서대로)
     */
    @Query("SELECT pi FROM PostImages pi WHERE pi.post.id = :postId ORDER BY pi.sortOrder ASC")
    List<PostImages> findByPostIdOrderBySortOrder(@Param("postId") Long postId);

    /**
     * 특정 게시글의 이미지 삭제
     */
    void deleteByPostId(Long postId);

    /**
     * 저장된 파일명으로 이미지 조회
     */
    boolean existsByStoredName(String storedName);
}

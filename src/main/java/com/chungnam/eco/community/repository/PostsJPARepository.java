package com.chungnam.eco.community.repository;

import com.chungnam.eco.community.domain.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostsJPARepository extends JpaRepository<Posts, Long> {

    /**
     * 게시글 목록을 페이징으로 조회 (최신순)
     */
    @Query("SELECT p FROM Posts p ORDER BY p.createdAt DESC")
    Page<Posts> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 게시글 상세 조회 (작성자 정보 포함)
     */
    @Query("SELECT p FROM Posts p JOIN FETCH p.member WHERE p.id = :postId")
    Optional<Posts> findByIdWithMember(@Param("postId") Long postId);

    /**
     * 특정 사용자의 게시글 목록 조회
     */
    @Query("SELECT p FROM Posts p WHERE p.member.id = :memberId ORDER BY p.createdAt DESC")
    Page<Posts> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId, Pageable pageable);
}

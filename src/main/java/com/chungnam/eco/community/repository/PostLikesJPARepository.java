package com.chungnam.eco.community.repository;

import com.chungnam.eco.community.domain.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikesJPARepository extends JpaRepository<PostLikes, Long> {

    /**
     * 특정 게시글과 사용자의 좋아요 조회
     */
    @Query("SELECT pl FROM PostLikes pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    Optional<PostLikes> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    /**
     * 특정 게시글과 사용자의 좋아요 존재 여부 확인
     */
    @Query("SELECT COUNT(pl) > 0 FROM PostLikes pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    /**
     * 특정 게시글의 좋아요 수 조회
     */
    @Query("SELECT COUNT(pl) FROM PostLikes pl WHERE pl.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    /**
     * 특정 게시글과 사용자의 좋아요 삭제
     */
    void deleteByPostIdAndMemberId(Long postId, Long memberId);
}

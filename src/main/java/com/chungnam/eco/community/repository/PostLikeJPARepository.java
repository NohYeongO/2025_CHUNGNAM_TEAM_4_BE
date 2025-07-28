package com.chungnam.eco.community.repository;

import com.chungnam.eco.community.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeJPARepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    Optional<PostLike> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}

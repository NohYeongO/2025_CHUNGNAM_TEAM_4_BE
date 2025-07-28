package com.chungnam.eco.community.repository;

import com.chungnam.eco.community.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageJPARepository extends JpaRepository<PostImage, Long> {
    @Query("SELECT pi FROM PostImage pi WHERE pi.post.id = :postId ORDER BY pi.sort")
    List<PostImage> findByPostIdOrderBySort(@Param("postId") Long postId);
}

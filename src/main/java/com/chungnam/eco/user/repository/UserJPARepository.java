package com.chungnam.eco.user.repository;

import com.chungnam.eco.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserJPARepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일 존재 여부 확인
     * @param email 확인할 이메일
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByEmail(String email);
    
    /**
     * 닉네임 존재 여부 확인
     * @param nickname 확인할 닉네임
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 닉네임으로 사용자 조회
     * @param nickname 조회할 닉네임
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByNickname(String nickname);
}

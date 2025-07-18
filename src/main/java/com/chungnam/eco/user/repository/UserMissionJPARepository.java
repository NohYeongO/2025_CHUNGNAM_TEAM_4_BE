package com.chungnam.eco.user.repository;

import com.chungnam.eco.user.domain.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMissionJPARepository extends JpaRepository<UserMission, Long> {

}

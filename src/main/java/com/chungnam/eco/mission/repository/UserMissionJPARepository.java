package com.chungnam.eco.mission.repository;

import com.chungnam.eco.mission.domain.MissionType;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.mission.domain.UserMission;
import com.chungnam.eco.mission.domain.UserMissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMissionJPARepository extends JpaRepository<UserMission, Long> {
    List<UserMission> findByUserAndMissionTypeAndStatusIn(User user, MissionType missionType, List<UserMissionStatus> statuses);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserMission um SET um.status = :newStatus WHERE um.missionType = :missionType AND um.status IN :statuses")
    int updateStatusForMissions(@Param("missionType") MissionType missionType, 
                                @Param("statuses") List<UserMissionStatus> statuses, 
                                @Param("newStatus") UserMissionStatus newStatus);
}

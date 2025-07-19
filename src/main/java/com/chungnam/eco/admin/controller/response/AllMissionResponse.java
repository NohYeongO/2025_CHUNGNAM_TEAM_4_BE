package com.chungnam.eco.admin.controller.response;

import com.chungnam.eco.mission.service.dto.MissionDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AllMissionResponse {
    private final List<MissionDto> mission_list;

    /**
     * 리스트 조회에 성공할 시 반환할 응답값
     */
    public static AllMissionResponse success(List<MissionDto> missionDtoList) {
        return AllMissionResponse.builder()
                .mission_list(missionDtoList)
                .build();
    }
}

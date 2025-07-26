package com.chungnam.eco.admin.service.dto;

import com.chungnam.eco.mission.domain.MissionCategory;
import com.chungnam.eco.mission.domain.MissionLevel;
import com.chungnam.eco.mission.domain.MissionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 전체 미션 리스트 응답을 감싸는 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIMissionListResponseDto {
    private List<AIMissionDto> missions;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIMissionDto {
        @JsonPropertyDescription("미션의 제목을 지정")
        private String title;

        @JsonPropertyDescription("미션의 설명 글과 인증 방법 작성")
        private String description;

        @JsonPropertyDescription("미션의 타입 지정")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private MissionType type;

        @JsonPropertyDescription("미션의 카테고리 지정")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private MissionCategory category;

        @JsonPropertyDescription("미션의 레벨 중 타입과 미션의 난이도를 선정하여 선택")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private MissionLevel level;

        @JsonPropertyDescription("미션 완료 시 지급할 보상 포인트, "
                + "미션의 타입 레벨 등을 고려하여 포인트 선정, "
                + "미션 타입이 DAILY 이면 20~100 사이 , WEEKLY 이면 100~300 사이")
        private Integer rewardPoints;
    }
}

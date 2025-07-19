package com.chungnam.eco.admin.controller.request;

import com.chungnam.eco.mission.domain.MissionCategory;
import com.chungnam.eco.mission.domain.MissionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMissionRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "설명을 입력해주세요.")
    private String description;

    @NotBlank(message = "타입을 지정해주세요.")
    private MissionType type;

    @NotBlank(message = "카테고리를 입력해주세요.")
    private MissionCategory category;

    @NotBlank(message = "보상을 입력해주세요.")
    private Integer rewardPoints;
}

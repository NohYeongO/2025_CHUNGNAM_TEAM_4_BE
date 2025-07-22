package com.chungnam.eco.user.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MissionSubmitRequest {
    
    @NotNull(message = "사용자 미션 ID는 필수입니다.")
    private Long userMissionId;
    
    @NotBlank(message = "설명글은 필수입니다.")
    @Size(max = 1000, message = "설명글은 1000자 이하로 입력해주세요.")
    private String description;
}

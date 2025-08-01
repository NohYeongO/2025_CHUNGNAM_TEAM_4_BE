package com.chungnam.eco.user.controller.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MissionChoiceRequest {
    @Size(max = 3, message = "일일 미션은 3개를 선택해야 합니다.")
    private List<Long> dailyMissionIds;

    @Size(max = 1, message = "주간 미션은 1개를 선택해야 합니다.")
    private List<Long> weeklyMissionIds;
}

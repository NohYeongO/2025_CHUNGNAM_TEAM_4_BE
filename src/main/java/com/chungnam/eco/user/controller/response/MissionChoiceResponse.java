package com.chungnam.eco.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionChoiceResponse {
    
    private final String message;
    private final int selectedDailyMissions;
    private final int selectedWeeklyMissions;
    
    /**
     * 미션 선택 성공
     */
    public static MissionChoiceResponse success(int dailyCount, int weeklyCount) {
        return MissionChoiceResponse.builder()
                .message("미션 선택이 완료되었습니다.")
                .selectedDailyMissions(dailyCount)
                .selectedWeeklyMissions(weeklyCount)
                .build();
    }
}

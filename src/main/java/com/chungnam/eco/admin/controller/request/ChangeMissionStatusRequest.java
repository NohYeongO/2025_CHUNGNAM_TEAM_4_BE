package com.chungnam.eco.admin.controller.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeMissionStatusRequest {
    List<Long> mission_list;
}

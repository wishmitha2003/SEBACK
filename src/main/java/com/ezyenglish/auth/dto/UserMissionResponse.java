package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionResponse {
    private String id;
    private String missionId;
    private String title;
    private String task;
    private Integer rewardXp;
    private Integer progress;
    private Integer goal;
    private boolean completed;
    private boolean rewardClaimed;
    private String type;
}

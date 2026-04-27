package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_missions")
public class UserMission {
    @Id
    @NonNull
    private String id;
    @NonNull
    private String userId;
    @NonNull
    private String missionId;
    private Integer progress;
    private boolean completed;
    private boolean rewardClaimed;
    @NonNull
    private LocalDate date;
}

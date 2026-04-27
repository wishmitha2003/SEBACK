package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "daily_missions")
public class DailyMission {
    @Id
    @NonNull
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String task;
    private Integer rewardXp;
    @NonNull
    private String type; // e.g., "VOCAB", "STREAK", "QUIZ"
    private Integer goal; // e.g., 10 for 10 words
}

package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationRecordingResponse {
    private String id;
    private String vocabularyId;
    private String userId;
    private String audioUrl;
    private String status;
    private LocalDateTime recordedAt;
    private LocalDateTime updatedAt;
}
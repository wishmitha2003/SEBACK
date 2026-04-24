package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationRecordingRequest {
    private String vocabularyId;
    private String audioUrl;
    private String status; // DRAFT, FINAL
}
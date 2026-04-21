package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pronunciation_recordings")
public class PronunciationRecording {
    @Id
    private String id;
    private String vocabularyId; // Reference to the vocabulary being practiced
    private String userId; // Student who recorded
    private String audioUrl; // URL to the recorded audio file
    private String status; // DRAFT, FINAL
    private LocalDateTime recordedAt;
    private LocalDateTime updatedAt;
}
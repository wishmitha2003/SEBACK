package com.ezyenglish.auth.service;

import com.ezyenglish.auth.dto.PronunciationRecordingRequest;
import com.ezyenglish.auth.dto.PronunciationRecordingResponse;
import com.ezyenglish.auth.model.PronunciationRecording;
import com.ezyenglish.auth.repository.PronunciationRecordingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PronunciationRecordingService {

    private final PronunciationRecordingRepository repository;
    private final MissionService missionService;

    public PronunciationRecordingResponse saveRecording(PronunciationRecordingRequest request, String userId) {
        log.info("Saving pronunciation recording for user: {} and vocabulary: {}", userId, request.getVocabularyId());
        
        // Check if user already has a recording for this vocabulary
        Optional<PronunciationRecording> existingRecording = 
            repository.findByUserIdAndVocabularyId(userId, request.getVocabularyId());
        
        PronunciationRecording recording;
        if (existingRecording.isPresent()) {
            // Update existing recording
            recording = existingRecording.get();
            recording.setAudioUrl(request.getAudioUrl());
            recording.setStatus(request.getStatus());
            recording.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new recording
            recording = PronunciationRecording.builder()
                    .vocabularyId(request.getVocabularyId())
                    .userId(userId)
                    .audioUrl(request.getAudioUrl())
                    .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                    .recordedAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
        
        PronunciationRecording saved = repository.save(recording);
        
        // Update mission progress
        missionService.updateMissionProgress(userId, "VOCAB", 1);
        missionService.updateMissionProgress(userId, "STREAK", 1);
        
        log.info("Saved pronunciation recording with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    public List<PronunciationRecordingResponse> getRecordingsByUser(String userId) {
        log.info("Fetching recordings for user: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PronunciationRecordingResponse> getRecordingsByVocabulary(String vocabularyId) {
        log.info("Fetching recordings for vocabulary: {}", vocabularyId);
        return repository.findByVocabularyId(vocabularyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<PronunciationRecordingResponse> getRecordingByUserAndVocabulary(String userId, String vocabularyId) {
        log.info("Fetching recording for user: {} and vocabulary: {}", userId, vocabularyId);
        return repository.findByUserIdAndVocabularyId(userId, vocabularyId)
                .map(this::mapToResponse);
    }

    public void deleteRecording(@org.springframework.lang.NonNull String id) {
        log.info("Deleting pronunciation recording: {}", id);
        repository.deleteById(id);
    }

    private PronunciationRecordingResponse mapToResponse(PronunciationRecording recording) {
        return PronunciationRecordingResponse.builder()
                .id(recording.getId())
                .vocabularyId(recording.getVocabularyId())
                .userId(recording.getUserId())
                .audioUrl(recording.getAudioUrl())
                .status(recording.getStatus())
                .recordedAt(recording.getRecordedAt())
                .updatedAt(recording.getUpdatedAt())
                .build();
    }
}
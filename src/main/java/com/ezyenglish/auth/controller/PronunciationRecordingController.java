package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.PronunciationRecordingRequest;
import com.ezyenglish.auth.dto.PronunciationRecordingResponse;
import com.ezyenglish.auth.service.PronunciationRecordingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/pronunciation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class PronunciationRecordingController {

    private final PronunciationRecordingService service;
    
    private static final String UPLOAD_DIR = "uploads/pronunciation/";
    private static final String BASE_URL = "http://localhost:8082";

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PronunciationRecordingResponse> saveRecording(
            @RequestPart(name = "vocabularyId") String vocabularyId,
            @RequestPart(name = "audioFile", required = false) MultipartFile audioFile,
            @RequestPart(name = "audioUrl", required = false) String audioUrl,
            @RequestPart(name = "status", required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Received request to save pronunciation recording for vocabulary: {}", vocabularyId);
        
        try {
            String finalAudioUrl = audioUrl;
            
            // If audio file is provided, save it
            if (audioFile != null && !audioFile.isEmpty()) {
                finalAudioUrl = saveAudioFile(audioFile, userDetails.getUsername());
            }
            
            if (finalAudioUrl == null || finalAudioUrl.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            PronunciationRecordingRequest request = PronunciationRecordingRequest.builder()
                    .vocabularyId(vocabularyId)
                    .audioUrl(finalAudioUrl)
                    .status(status != null ? status : "DRAFT")
                    .build();
            
            PronunciationRecordingResponse response = service.saveRecording(request, userDetails.getUsername());
            log.info("Successfully saved pronunciation recording: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            log.error("Failed to save audio file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-recordings")
    public ResponseEntity<List<PronunciationRecordingResponse>> getMyRecordings(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching recordings for user: {}", userDetails.getUsername());
        List<PronunciationRecordingResponse> recordings = service.getRecordingsByUser(userDetails.getUsername());
        return ResponseEntity.ok(recordings);
    }

    @GetMapping("/vocabulary/{vocabularyId}")
    public ResponseEntity<List<PronunciationRecordingResponse>> getRecordingsByVocabulary(
            @PathVariable String vocabularyId) {
        log.info("Fetching recordings for vocabulary: {}", vocabularyId);
        List<PronunciationRecordingResponse> recordings = service.getRecordingsByVocabulary(vocabularyId);
        return ResponseEntity.ok(recordings);
    }

    @GetMapping("/vocabulary/{vocabularyId}/my")
    public ResponseEntity<PronunciationRecordingResponse> getMyRecordingForVocabulary(
            @PathVariable String vocabularyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching my recording for vocabulary: {}", vocabularyId);
        return service.getRecordingByUserAndVocabulary(userDetails.getUsername(), vocabularyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecording(
            @PathVariable @org.springframework.lang.NonNull String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to delete recording: {}", id);
        service.deleteRecording(id);
        return ResponseEntity.noContent().build();
    }

    private String saveAudioFile(MultipartFile file, String username) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = username + "_" + UUID.randomUUID() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        log.info("Saved audio file: {}", filePath);
        return BASE_URL + "/uploads/pronunciation/" + filename;
    }
}
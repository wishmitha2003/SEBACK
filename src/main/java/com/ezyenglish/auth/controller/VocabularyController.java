package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.VocabularyRequest;
import com.ezyenglish.auth.model.Vocabulary;
import com.ezyenglish.auth.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class VocabularyController {

    private final VocabularyService service;

    // All users (students and teachers) can view all vocabularies
    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabularies() {
        log.info("Request received to fetch all vocabularies");
        return ResponseEntity.ok(service.getAllVocabularies());
    }

    // All users can view vocabularies by age section
    @GetMapping("/age/{ageSection}")
    public ResponseEntity<List<Vocabulary>> getVocabulariesByAgeSection(@PathVariable @org.springframework.lang.NonNull String ageSection) {
        log.info("Request received to fetch vocabularies for age section: {}", ageSection);
        return ResponseEntity.ok(service.getVocabulariesByAgeSection(ageSection));
    }

    // Only teachers can add vocabularies
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Vocabulary> createVocabulary(
            @RequestBody VocabularyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request received to create vocabulary by user: {}", userDetails.getUsername());
        Vocabulary vocabulary = Vocabulary.builder()
                .word(request.getWord())
                .meaning(request.getMeaning())
                .example(request.getExample())
                .ageSection(request.getAgeSection())
                .build();
        Vocabulary created = service.createVocabulary(vocabulary, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Only teachers can update vocabularies
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Vocabulary> updateVocabulary(
            @PathVariable @org.springframework.lang.NonNull String id,
            @RequestBody VocabularyRequest request) {
        log.info("Request received to update vocabulary: {}", id);
        Vocabulary vocabulary = Vocabulary.builder()
                .word(request.getWord())
                .meaning(request.getMeaning())
                .example(request.getExample())
                .ageSection(request.getAgeSection())
                .build();
        Vocabulary updated = service.updateVocabulary(id, vocabulary);
        return ResponseEntity.ok(updated);
    }

    // Only teachers can delete vocabularies
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable @org.springframework.lang.NonNull String id) {
        log.info("Request received to delete vocabulary: {}", id);
        service.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }
}
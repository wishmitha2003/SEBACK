package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.Vocabulary;
import com.ezyenglish.auth.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;

    public List<Vocabulary> getAllVocabularies() {
        log.info("Fetching all vocabularies");
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public List<Vocabulary> getVocabulariesByAgeSection(String ageSection) {
        log.info("Fetching vocabularies for age section: {}", ageSection);
        return repository.findByAgeSection(ageSection);
    }

    public Vocabulary getVocabularyById(String id) {
        log.info("Fetching vocabulary by id: {}", id);
        return repository.findById(id).orElse(null);
    }

    public Vocabulary createVocabulary(Vocabulary vocabulary, String addedBy) {
        log.info("Creating new vocabulary: {} for age section: {}", vocabulary.getWord(), vocabulary.getAgeSection());
        vocabulary.setAddedBy(addedBy);
        vocabulary.setCreatedAt(LocalDateTime.now());
        vocabulary.setUpdatedAt(LocalDateTime.now());
        return repository.save(vocabulary);
    }

    public Vocabulary updateVocabulary(String id, Vocabulary vocabulary) {
        log.info("Updating vocabulary with id: {}", id);
        Vocabulary existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
        
        existing.setWord(vocabulary.getWord());
        existing.setMeaning(vocabulary.getMeaning());
        existing.setExample(vocabulary.getExample());
        existing.setAgeSection(vocabulary.getAgeSection());
        existing.setUpdatedAt(LocalDateTime.now());
        
        return repository.save(existing);
    }

    public void deleteVocabulary(String id) {
        log.info("Deleting vocabulary with id: {}", id);
        repository.deleteById(id);
    }
}
package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.PronunciationRecording;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PronunciationRecordingRepository extends MongoRepository<PronunciationRecording, String> {
    List<PronunciationRecording> findByUserId(String userId);
    List<PronunciationRecording> findByVocabularyId(String vocabularyId);
    Optional<PronunciationRecording> findByUserIdAndVocabularyId(String userId, String vocabularyId);
}
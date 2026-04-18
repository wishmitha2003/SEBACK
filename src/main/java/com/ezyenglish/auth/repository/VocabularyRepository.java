package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.Vocabulary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
    List<Vocabulary> findByAgeSection(String ageSection);
    List<Vocabulary> findAllByOrderByCreatedAtDesc();
}
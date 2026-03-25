package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.LanguageClass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageClassRepository extends MongoRepository<LanguageClass, String> {
}

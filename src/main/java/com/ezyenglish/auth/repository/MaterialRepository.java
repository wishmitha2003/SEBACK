package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.Material;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends MongoRepository<Material, String> {
}

package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.DailyMission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyMissionRepository extends MongoRepository<DailyMission, String> {
    List<DailyMission> findByType(String type);
}

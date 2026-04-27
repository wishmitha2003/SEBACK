package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.UserMission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends MongoRepository<UserMission, String> {
    List<UserMission> findByUserIdAndDate(String userId, LocalDate date);
    Optional<UserMission> findByUserIdAndMissionIdAndDate(String userId, String missionId, LocalDate date);
}

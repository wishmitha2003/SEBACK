package com.ezyenglish.auth.service;

import com.ezyenglish.auth.dto.UserMissionResponse;
import com.ezyenglish.auth.model.DailyMission;
import com.ezyenglish.auth.model.User;
import com.ezyenglish.auth.model.UserMission;
import com.ezyenglish.auth.repository.DailyMissionRepository;
import com.ezyenglish.auth.repository.UserMissionRepository;
import com.ezyenglish.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class MissionService {

    private final DailyMissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;

    public List<UserMissionResponse> getDailyMissions(@NonNull String userId) {
        LocalDate today = LocalDate.now();
        List<UserMission> userMissions = userMissionRepository.findByUserIdAndDate(userId, today);

        if (userMissions.isEmpty()) {
            // Initialize missions for today
            userMissions = initializeMissionsForUser(userId, today);
        }

        return userMissions.stream().map(um -> {
            String missionId = um.getMissionId();
            DailyMission dm = missionRepository.findById(missionId).orElse(null);
            return UserMissionResponse.builder()
                    .id(um.getId())
                    .missionId(um.getMissionId())
                    .title(dm != null ? dm.getTitle() : "Unknown Mission")
                    .task(dm != null ? dm.getTask() : "")
                    .rewardXp(dm != null ? dm.getRewardXp() : 0)
                    .progress(um.getProgress())
                    .goal(dm != null ? dm.getGoal() : 100)
                    .completed(um.isCompleted())
                    .rewardClaimed(um.isRewardClaimed())
                    .type(dm != null ? dm.getType() : "GENERAL")
                    .build();
        }).collect(Collectors.toList());
    }

    private List<UserMission> initializeMissionsForUser(@NonNull String userId, @NonNull LocalDate date) {
        List<DailyMission> availableMissions = missionRepository.findAll();
        if (availableMissions.isEmpty()) {
            seedDefaultMissions();
            availableMissions = missionRepository.findAll();
        }

        List<UserMission> userMissions = new ArrayList<>();
        // Assign all available missions for today
        for (DailyMission mission : availableMissions) {
            UserMission um = UserMission.builder()
                    .userId(userId)
                    .missionId(mission.getId())
                    .progress(0)
                    .completed(false)
                    .rewardClaimed(false)
                    .date(date)
                    .build();
            UserMission saved = userMissionRepository.save(um);
            userMissions.add(saved);
        }
        return userMissions;
    }

    private void seedDefaultMissions() {
        DailyMission vocabMission = DailyMission.builder()
                .title("Vocab Master")
                .task("Learn 10 new words today")
                .rewardXp(50)
                .type("VOCAB")
                .goal(10)
                .build();
        missionRepository.save(vocabMission);

        DailyMission streakMission = DailyMission.builder()
                .title("Streak Keeper")
                .task("Complete one lesson")
                .rewardXp(30)
                .type("STREAK")
                .goal(1)
                .build();
        missionRepository.save(streakMission);

        DailyMission grammarMission = DailyMission.builder()
                .title("Grammar Guru")
                .task("Pass a Grammar quiz with 90%+")
                .rewardXp(100)
                .type("QUIZ")
                .goal(1)
                .build();
        missionRepository.save(grammarMission);
    }

    public void updateMissionProgress(@NonNull String userId, @NonNull String type, int increment) {
        LocalDate today = LocalDate.now();
        List<UserMission> userMissions = userMissionRepository.findByUserIdAndDate(userId, today);
        
        if (userMissions.isEmpty()) {
            userMissions = initializeMissionsForUser(userId, today);
        }

        for (UserMission um : userMissions) {
            String missionId = um.getMissionId();
            DailyMission dm = missionRepository.findById(missionId).orElse(null);
            if (dm != null && dm.getType().equals(type)) {
                if (!um.isCompleted()) {
                    um.setProgress(um.getProgress() + increment);
                    if (um.getProgress() >= dm.getGoal()) {
                        um.setProgress(dm.getGoal());
                        um.setCompleted(true);
                    }
                    userMissionRepository.save(um);
                }
            }
        }
    }

    public void claimReward(@NonNull String userId, @NonNull String userMissionId) {
        UserMission um = userMissionRepository.findById(userMissionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));
        
        if (!um.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (um.isCompleted() && !um.isRewardClaimed()) {
            String missionId = um.getMissionId();
            DailyMission dm = missionRepository.findById(missionId).orElse(null);
            if (dm != null) {
                User user = userRepository.findByUsername(userId).orElseThrow();
                user.setXp(user.getXp() + dm.getRewardXp());
                userRepository.save(user);
                
                um.setRewardClaimed(true);
                userMissionRepository.save(um);
            }
        }
    }
}

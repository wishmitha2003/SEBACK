package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.MessageResponse;
import com.ezyenglish.auth.dto.UserMissionResponse;
import com.ezyenglish.auth.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/daily")
    @PreAuthorize("hasRole('STUDENT') or hasRole('USER')")
    public ResponseEntity<List<UserMissionResponse>> getDailyMissions(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(missionService.getDailyMissions(username));
    }

    @PostMapping("/claim/{userMissionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('USER')")
    public ResponseEntity<?> claimReward(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String userMissionId) {
        String username = userDetails.getUsername();
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        missionService.claimReward(username, userMissionId);
        return ResponseEntity.ok(new MessageResponse("Reward claimed successfully!"));
    }
}

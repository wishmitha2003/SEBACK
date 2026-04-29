package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.LeaderboardEntryResponse;
import com.ezyenglish.auth.dto.MyRankResponse;
import com.ezyenglish.auth.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for leaderboard endpoints.
 *
 * <pre>
 * GET  /api/leaderboard                  – top-N XP leaderboard (default 50)
 * GET  /api/leaderboard?type=STREAK      – top-N streak leaderboard
 * GET  /api/leaderboard?limit=20         – custom limit (max 100)
 * GET  /api/leaderboard/me               – current user's rank + surrounding
 * </pre>
 */
@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/leaderboard
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the global leaderboard.
     *
     * @param type  "XP" (default) or "STREAK"
     * @param limit number of entries to return (1–100, default 50)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "XP")   String type,
            @RequestParam(defaultValue = "50")    int    limit) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUser = userDetails.getUsername();
        List<LeaderboardEntryResponse> board;

        if ("STREAK".equalsIgnoreCase(type)) {
            board = leaderboardService.getStreakLeaderboard(limit, currentUser);
        } else {
            board = leaderboardService.getXpLeaderboard(limit, currentUser);
        }

        return ResponseEntity.ok(board);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/leaderboard/me
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the authenticated user's current XP rank plus 2 players above
     * and 2 players below for competitive context.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MyRankResponse> getMyRank(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MyRankResponse response = leaderboardService.getMyRank(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}

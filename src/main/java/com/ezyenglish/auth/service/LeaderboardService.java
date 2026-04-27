package com.ezyenglish.auth.service;

import com.ezyenglish.auth.dto.LeaderboardEntryResponse;
import com.ezyenglish.auth.dto.MyRankResponse;
import com.ezyenglish.auth.model.User;
import com.ezyenglish.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class LeaderboardService {

    private static final int DEFAULT_LIMIT    = 50;
    private static final int MAX_LIMIT        = 100;
    private static final int SURROUNDING_SIZE = 2;   // players above & below me

    private final UserRepository userRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the top-N leaderboard sorted by XP (descending).
     *
     * @param limit      number of entries to return (capped at {@code MAX_LIMIT})
     * @param currentUser username of the requesting user (used to mark own entry)
     */
    public List<LeaderboardEntryResponse> getXpLeaderboard(int limit, String currentUser) {
        int safeLimit = clamp(limit);
        // Sort is defined by the method name (OrderByXpDesc) — no Sort in PageRequest needed
        List<User> users = userRepository.findAllByOrderByXpDesc(
                PageRequest.of(0, safeLimit)
        );
        return mapToLeaderboard(users, currentUser, "XP");
    }

    /**
     * Returns the top-N leaderboard sorted by streak (descending).
     *
     * @param limit      number of entries to return (capped at {@code MAX_LIMIT})
     * @param currentUser username of the requesting user (used to mark own entry)
     */
    public List<LeaderboardEntryResponse> getStreakLeaderboard(int limit, String currentUser) {
        int safeLimit = clamp(limit);
        // Sort is defined by the method name (OrderByStreakDesc) — no Sort in PageRequest needed
        List<User> users = userRepository.findAllByOrderByStreakDesc(
                PageRequest.of(0, safeLimit)
        );
        return mapToLeaderboard(users, currentUser, "STREAK");
    }

    /**
     * Returns the requesting user's XP rank plus {@code SURROUNDING_SIZE} players
     * directly above and below them for context.
     *
     * @param username the username of the authenticated user
     */
    public MyRankResponse getMyRank(String username) {
        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 1-based rank: count users with strictly higher XP, then add 1
        long higherCount   = userRepository.countByXpGreaterThan(me.getXp());
        int  myRank        = (int) higherCount + 1;
        long totalPlayers  = userRepository.count();

        // Build my own entry
        LeaderboardEntryResponse myEntry = toEntry(me, myRank, true);

        // Fetch a window of players centred around my rank for the surrounding list
        List<LeaderboardEntryResponse> surrounding = buildSurroundingWindow(me, myRank, totalPlayers, username);

        return MyRankResponse.builder()
                .myRank(myRank)
                .totalPlayers((int) totalPlayers)
                .myEntry(myEntry)
                .surrounding(surrounding)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetches a slice of the XP leaderboard that is guaranteed to include the
     * current user, plus {@code SURROUNDING_SIZE} entries on each side.
     */
    private List<LeaderboardEntryResponse> buildSurroundingWindow(
            User me, int myRank, long totalPlayers, String username) {

        // Window: [myRank - SURROUNDING_SIZE, myRank + SURROUNDING_SIZE]
        int windowStart = Math.max(1, myRank - SURROUNDING_SIZE);          // 1-based
        int windowEnd   = (int) Math.min(totalPlayers, myRank + SURROUNDING_SIZE);
        int windowSize  = windowEnd - windowStart + 1;

        // Fetch ranks 1..windowEnd from DB; slice the window out afterward
        // Sort is defined by method name — no Sort in PageRequest needed
        List<User> windowUsers = userRepository.findAllByOrderByXpDesc(
                PageRequest.of(0, windowStart + windowSize - 1)
        );

        // Slice out only the window portion
        int fromIdx = windowStart - 1;              // convert to 0-based
        int toIdx   = Math.min(fromIdx + windowSize, windowUsers.size());
        List<User> sliced = windowUsers.subList(fromIdx, toIdx);

        List<LeaderboardEntryResponse> result = new ArrayList<>();
        for (int i = 0; i < sliced.size(); i++) {
            User u       = sliced.get(i);
            int  rank    = windowStart + i;
            boolean isMe = u.getUsername().equals(username);
            result.add(toEntry(u, rank, isMe));
        }
        return result;
    }

    /**
     * Maps a list of {@link User} objects to {@link LeaderboardEntryResponse},
     * assigning sequential rank numbers starting at 1.
     * Users with equal scores get the same rank (standard competition ranking).
     */
    private List<LeaderboardEntryResponse> mapToLeaderboard(
            List<User> users, String currentUser, String sortBy) {

        List<LeaderboardEntryResponse> result = new ArrayList<>();
        int rank          = 1;
        int prevScore     = -1;
        int sameRankCount = 0;

        for (int i = 0; i < users.size(); i++) {
            User u     = users.get(i);
            int  score = "STREAK".equals(sortBy) ? safeInt(u.getStreak()) : safeInt(u.getXp());

            if (score == prevScore) {
                // Tie – same rank as the previous entry
                sameRankCount++;
            } else {
                rank      = i + 1;
                prevScore = score;
                sameRankCount = 0;
            }

            boolean isMe = u.getUsername().equals(currentUser);
            result.add(toEntry(u, rank, isMe));
        }
        return result;
    }

    /** Converts a {@link User} to a {@link LeaderboardEntryResponse}. */
    private LeaderboardEntryResponse toEntry(User u, int rank, boolean isCurrentUser) {
        return LeaderboardEntryResponse.builder()
                .rank(rank)
                .userId(u.getId())
                .username(u.getUsername())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .profileImageUrl(u.getProfileImageUrl())
                .xp(safeInt(u.getXp()))
                .streak(safeInt(u.getStreak()))
                .currentUser(isCurrentUser)
                .build();
    }

    /** Clamps the requested limit to the allowed range [1, MAX_LIMIT]. */
    private int clamp(int limit) {
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }

    /** Null-safe int helper for XP / streak fields that default to 0. */
    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}

package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // ── Leaderboard queries ──────────────────────────────────────────────────

    /** Top N users sorted by XP descending (for XP leaderboard). */
    List<User> findTop10ByOrderByXpDesc();

    /** Pageable version – supports any limit for XP leaderboard. */
    List<User> findAllByOrderByXpDesc(Pageable pageable);

    /** Pageable version – supports any limit for streak leaderboard. */
    List<User> findAllByOrderByStreakDesc(Pageable pageable);

    /** Count users whose XP is strictly greater – used to compute rank. */
    long countByXpGreaterThan(Integer xp);

    /** Count users whose streak is strictly greater – used to compute streak rank. */
    long countByStreakGreaterThan(Integer streak);
}

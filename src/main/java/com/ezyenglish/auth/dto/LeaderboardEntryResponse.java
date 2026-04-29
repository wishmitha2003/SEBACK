package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private int rank;
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private Integer xp;
    private Integer streak;
    private boolean currentUser;
}

package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyRankResponse {
    private int myRank;
    private int totalPlayers;
    private LeaderboardEntryResponse myEntry;
    private List<LeaderboardEntryResponse> surrounding; // 2 above + me + 2 below
}

package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RankingRow", description = "A ranking row")
public record RankingRowDTO(
        TeamDTO team,
        int rank,
        int matchPlayedCount,
        int matchWonCount,
        int matchLostCount,
        int drawCount,
        int goalForCount,
        int goalAgainstCount,
        int goalDifference,
        int points
) {}
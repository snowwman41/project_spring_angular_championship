package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(name = "Match", description = "A match")
public record MatchDTO(
        UUID id,
        TeamDTO homeTeam,
        TeamDTO awayTeam,
        int homeTeamGoals,
        int awayTeamGoals,
        LocalDate date,
        LocalTime time
){}

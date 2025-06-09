package com.project.controllers;

import com.project.dto.ErrorDTO;
import com.project.dto.MatchDTO;
import com.project.dto.RankingRowDTO;
import com.project.dto.TeamDTO;
import com.project.services.SoccerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("/api")

public class SoccerApiController {
    private final SoccerService soccerService;

    public SoccerApiController(SoccerService soccerService) {
        this.soccerService = soccerService;
    }

    @GetMapping(value = "/ranking", produces = "application/json")
    @Operation(summary = "Get the ranking")
    @ApiResponse(responseCode = "200", description = "The ranking")
    public List<RankingRowDTO> ranking() {
        /* TODO */
        return soccerService.getRanking();
    }
    @GetMapping(value = "/teams", produces = "application/json")
    @Operation(summary = "Get the teams")
    @ApiResponse(responseCode = "200", description = "The teams")
    public List<TeamDTO> teams() {
        return soccerService.getTeams();
    }
    @GetMapping(value = "/teams/{teamId}/ranking", produces = "application/json")
    @ApiResponse(responseCode = "404", description = "Team not found", content = {@Content()})
    @Operation(summary = "Get the ranking row of the team of identifier {teamId}")
    @ApiResponse(responseCode = "200", description = "The ranking row of the team")
    public RankingRowDTO rankingRow(@PathVariable("teamId") UUID teamId) {
        return soccerService.getRankingRow(teamId);
    }
    @GetMapping(value = "/teams/{teamId}/matches", produces = "application/json")
    @Operation(summary = "Get the matches played by team of identifier {teamId} ordered by date")
    @ApiResponse(responseCode = "200", description = "The matches played by the team")
    public List<MatchDTO> teamMatches(@PathVariable("teamId") UUID teamId) {
        return soccerService.getMatches(teamId);
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorDTO handleNoSuchElement(NoSuchElementException exception) {
        return new ErrorDTO(404, "Not found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handleException (Exception  exception) {
        return new ErrorDTO(500, "Internal server error");
    }

}


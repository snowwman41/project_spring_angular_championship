package com.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.config.SecurityConfig;
import com.project.dto.ErrorDTO;
import com.project.dto.MatchDTO;
import com.project.dto.RankingRowDTO;
import com.project.dto.TeamDTO;
import com.project.services.SoccerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SoccerApiController.class, SecurityConfig.class})
public class SoccerApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private SoccerService service;

    @Test
    public void testRanking() throws Exception {
        // Given
        UUID teamId0 =  UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        UUID teamId1 =  UUID.fromString("aabd33ba-3c89-43e7-903d-2ce15295128e");
        TeamDTO team0 = new TeamDTO(teamId0, "Marseille");
        TeamDTO team1 = new TeamDTO(teamId1, "Paris");
        List<RankingRowDTO> rows = List.of(
                new RankingRowDTO(team0,
                        3, 38, 22, 10,
                        6, 111, 92, 19, 72),
                new RankingRowDTO(team1,
                        5, 38, 19, 15,
                        4, 86, 80, 6, 61)
        );
        when(service.getRanking()).thenReturn(rows);
        // When / Then
        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(rows)));
        verify(service, times(1)).getRanking();
    }

    @Test
    public void testTeams() throws Exception {
        // Given
        UUID teamId0 =  UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        UUID teamId1 =  UUID.fromString("aabd33ba-3c89-43e7-903d-2ce15295128e");
        List<TeamDTO> teams = List.of(
                new TeamDTO(teamId0, "Marseille"),
                new TeamDTO(teamId1, "Paris")
        );
        when(service.getTeams()).thenReturn(teams);
        // When / Then
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(teams)));
        verify(service, times(1)).getTeams();
    }

    @Test
    public void testRankingRow() throws Exception {
        // Given
        UUID teamId =  UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        TeamDTO team = new TeamDTO(teamId, "Marseille");
        RankingRowDTO rankingRow = new RankingRowDTO(team,
                3, 38, 22, 10,
                6, 111, 92, 19, 72);
        when(service.getRankingRow(teamId)).thenReturn(rankingRow);
        // When / Then
        mockMvc.perform(get("/api/teams/"+teamId+"/ranking"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(rankingRow)));
        verify(service, times(1)).getRankingRow(teamId);
    }

    @Test
    public void testTeamMatches() throws Exception {
        // Given
        UUID teamId0 =  UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        UUID teamId1 =  UUID.fromString("aabd33ba-3c89-43e7-903d-2ce15295128e");
        UUID matchId0 = UUID.fromString("aabd31ba-3c89-43e7-903d-2ce15295128e");
        UUID matchId1 = UUID.fromString("aabd36ba-3c89-43e7-903d-2ce15295128e");
        TeamDTO team0 = new TeamDTO(teamId0, "Marseille");
        TeamDTO team1 = new TeamDTO(teamId1, "Paris");
        List<MatchDTO> matches = List.of(
                new MatchDTO(matchId0, team0, team1,0, 0, LocalDate.now(), LocalTime.now()),
                new MatchDTO(matchId1, team1, team0,0, 0, LocalDate.now(), LocalTime.now())
        );
        when(service.getMatches(teamId0)).thenReturn(matches);
        // When / Then
        mockMvc.perform(get("/api/teams/"+teamId0+"/matches"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(matches)));
        verify(service, times(1)).getMatches(teamId0);
    }
    @Test
    public void testHandleNoSuchElementException() throws Exception {
        // Given
        UUID teamId =  UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        when(service.getRankingRow(teamId)).thenThrow(new NoSuchElementException());
        // When / Then
        mockMvc.perform(get("/api/teams/"+teamId+"/ranking"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(new ErrorDTO(404, "Not found"))));
        verify(service, times(1)).getRankingRow(teamId);
    }

    @Test
    public void testHandleException() throws Exception {
        // Given
        when(service.getRanking()).thenThrow(new RuntimeException());
        // When / Then
        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(objectMapper.writeValueAsString(new ErrorDTO(500, "Internal server error"))));
        verify(service, times(1)).getRanking();
    }
}
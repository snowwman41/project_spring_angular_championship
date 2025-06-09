package com.project.controllers;

import com.project.dto.MatchCreationDTO;
import com.project.dto.MatchDTO;
import com.project.services.SoccerService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import com.project.dto.TeamDTO;
import com.project.dto.RankingRowDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WithMockUser
@WebMvcTest
@Import(SoccerController.class)
public class SoccerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SoccerService service;

    @Test
    public void testRanking() throws Exception {
        // Given
        UUID teamId0 = UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        UUID teamId1 = UUID.fromString("aabd33ba-3c89-43e7-903d-2ce15295128e");
        List<RankingRowDTO> rows = List.of(
                new RankingRowDTO(new TeamDTO(teamId0, "Marseille"),
                        3, 38, 22, 10, 6, 111, 92, 19, 72),
                new RankingRowDTO(new TeamDTO(teamId1, "Paris"),
                        5, 38, 19, 15, 4, 86, 80, 6, 61)
        );
        when(service.getRanking()).thenReturn(rows);
        // When
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse().getContentAsString();
        // Then
        assertThat(html, stringContainsInOrder("N°", "Équipe", "MJ", "G", "N", "P", "BP", "BC", "DB", "Pts"));
        assertThat(html, stringContainsInOrder("3", "<a href=\"/team/" + teamId0 + "\">Marseille</a>", "38", "22", "6", "10", "111", "92", "19", "72"));
        assertThat(html, stringContainsInOrder("5", "<a href=\"/team/" + teamId1 + "\">Paris</a>", "38", "19", "4", "15", "86", "80", "6", "61"));
    }

    @Test
    public void testTeam() throws Exception {
//        // When
//        MvcResult result = mockMvc.perform(get("/team/aabd33ba-2c89-43e7-903d-0cd15295128e"))
//                .andExpect(status().isOk())
//                .andReturn();
//        String html = result.getResponse().getContentAsString();
//        // Then
//        assertThat(html, containsString("aabd33ba-2c89-43e7-903d-0cd15295128e"));
        // Given
        UUID matchId1 = UUID.fromString("de5045c8-59be-46c4-83af-2c6f5da3411b");
        UUID matchId2 = UUID.fromString("de5045c8-5969-46c1-83af-2c6f5da3411b");

        UUID teamId1 = UUID.fromString("aabd33ba-2c89-43e7-903d-0cd15295128e");
        UUID teamId2 = UUID.fromString("78416886-d57a-43b2-8631-ea392957f4b1");

        TeamDTO team1 = new TeamDTO(teamId1, "Marseille");
        TeamDTO team2 = new TeamDTO(teamId2, "Le Havre");

        List<MatchDTO> matches = List.of(new MatchDTO(
                matchId1,
                team1,
                team2,
                1, 4, LocalDate.of(2048, 8, 3), LocalTime.of(21, 0)
        ), new MatchDTO(
                matchId2,
                team2,
                team1,
                3, 1, LocalDate.of(2048, 8, 10), LocalTime.of(21, 0)
        ));

        RankingRowDTO rankingRow = new RankingRowDTO(
                team1, 3, 38, 22, 10, 6, 111, 92, 19, 72);
        when(service.getRankingRow(teamId1)).thenReturn(rankingRow);
        when(service.getMatches(teamId1)).thenReturn(matches);
        // When
        MvcResult result = mockMvc.perform(get("/team/aabd33ba-2c89-43e7-903d-0cd15295128e"))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse().getContentAsString();

        // Then
        assertThat(html, stringContainsInOrder("N°", "Équipe", "MJ", "G", "N", "P", "BP", "BC", "DB", "Pts"));
        assertThat(html, stringContainsInOrder("3", "Marseille", "38", "22", "6", "10", "111", "92", "19", "72"));
        assertThat(html, stringContainsInOrder("03/08/2048 21:00", "Marseille", "1 - 4", "Le Havre"));
        assertThat(html, stringContainsInOrder("10/08/2048 21:00", "Le Havre", "3 - 1", "Marseille"));


    }
    @Test
    void testShowAddTeamForm() throws Exception {
        mockMvc.perform(get("/admin/team/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-team"))
                .andExpect(model().attributeExists("teamDTO"));
    }

    @Test
    void testAddTeam() throws Exception {
        TeamDTO teamDTO = new TeamDTO(null, "AAA");
        mockMvc.perform(post("/admin/team/add").with(csrf())
                        .param("name", teamDTO.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(service, times(1)).addTeam(teamDTO);
    }

    @Test
    void testAddTeamWithInvalidData() throws Exception {
        TeamDTO teamDTO = new TeamDTO(null, "A");
        mockMvc.perform(post("/admin/team/add").with(csrf())
                        .param("name", "A"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-team"))
                .andExpect(model().attributeExists("teamDTO"))
                .andExpect(model().attributeHasFieldErrors("teamDTO", "name"))
                .andExpect(model().attribute("teamDTO", teamDTO));
        verify(service, times(0)).addTeam(any());
    }

    @Test
    void testAddTeamWithException() throws Exception {
        TeamDTO teamDTO = new TeamDTO(null, "AAA");
        doThrow(new RuntimeException()).when(service).addTeam(teamDTO);
        mockMvc.perform(post("/admin/team/add").with(csrf())
                        .param("name", teamDTO.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-team"))
                .andExpect(model().attributeExists("teamDTO"))
                .andExpect(model().attributeHasErrors("teamDTO"))
                .andExpect(model().attribute("teamDTO", teamDTO));
        verify(service, times(1)).addTeam(teamDTO);
    }
    @Test
    void testShowAddMatchForm() throws Exception {
        List<TeamDTO> teams = List.of(new TeamDTO(UUID.randomUUID(), "Marseille"));
        when(service.getTeams()).thenReturn(teams);
        mockMvc.perform(get("/admin/match/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-match"))
                .andExpect(model().attributeExists("matchCreationDTO"))
                .andExpect(model().attributeExists("teams"))
                .andExpect(model().attribute("teams", teams));
        verify(service, times(1)).getTeams();
    }

    @Test
    void testAddMatch() throws Exception {
        TeamDTO team1 = new TeamDTO(UUID.randomUUID(), "AAA");
        TeamDTO team2 = new TeamDTO(UUID.randomUUID(), "BBB");
        MatchCreationDTO matchCreationDTO = new MatchCreationDTO(null,
                team1.id(), team2.id(),
                0, 0, LocalDate.now(), LocalTime.now());
        mockMvc.perform(post("/admin/match/add").with(csrf())
                        .param("homeTeamId", matchCreationDTO.homeTeamId().toString())
                        .param("awayTeamId", matchCreationDTO.awayTeamId().toString())
                        .param("homeTeamGoals", matchCreationDTO.homeTeamGoals().toString())
                        .param("awayTeamGoals", matchCreationDTO.awayTeamGoals().toString())
                        .param("date", matchCreationDTO.date().toString())
                        .param("time", matchCreationDTO.time().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(service, times(1)).addMatch(matchCreationDTO);
    }

    @Test
    void testAddMatchWithInvalidData() throws Exception {
        TeamDTO team1 = new TeamDTO(UUID.randomUUID(), "AAA");
        TeamDTO team2 = new TeamDTO(UUID.randomUUID(), "BBB");
        MatchCreationDTO matchCreationDTO = new MatchCreationDTO(
                null, team1.id(), team2.id(),
                0, 70, LocalDate.now(), LocalTime.now());
        doThrow(new RuntimeException()).when(service).addMatch(matchCreationDTO);
        mockMvc.perform(post("/admin/match/add").with(csrf())
                        .param("homeTeamId", matchCreationDTO.homeTeamId().toString())
                        .param("awayTeamId", matchCreationDTO.awayTeamId().toString())
                        .param("homeTeamGoals", matchCreationDTO.homeTeamGoals().toString())
                        .param("awayTeamGoals", matchCreationDTO.awayTeamGoals().toString())
                        .param("date", matchCreationDTO.date().toString())
                        .param("time", matchCreationDTO.time().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-match"))
                .andExpect(model().attributeExists("matchCreationDTO"))
                .andExpect(model().attributeHasErrors("matchCreationDTO"))
                .andExpect(model().attribute("matchCreationDTO", matchCreationDTO));
        verify(service, times(0)).addMatch(matchCreationDTO);
    }

    @Test
    void testAddMatchWithException() throws Exception {
        TeamDTO team1 = new TeamDTO(UUID.randomUUID(), "AAA");
        TeamDTO team2 = new TeamDTO(UUID.randomUUID(), "BBB");
        MatchCreationDTO matchCreationDTO = new MatchCreationDTO(null, team1.id(), team2.id(),
                0, 0, LocalDate.now(), LocalTime.now());
        doThrow(new RuntimeException()).when(service).addMatch(matchCreationDTO);
        mockMvc.perform(post("/admin/match/add").with(csrf())
                        .param("homeTeamId", matchCreationDTO.homeTeamId().toString())
                        .param("awayTeamId", matchCreationDTO.awayTeamId().toString())
                        .param("homeTeamGoals", matchCreationDTO.homeTeamGoals().toString())
                        .param("awayTeamGoals", matchCreationDTO.awayTeamGoals().toString())
                        .param("date", matchCreationDTO.date().toString())
                        .param("time", matchCreationDTO.time().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-match"))
                .andExpect(model().attributeExists("matchCreationDTO"))
                .andExpect(model().attributeHasErrors("matchCreationDTO"))
                .andExpect(model().attribute("matchCreationDTO", matchCreationDTO));
        verify(service, times(1)).addMatch(matchCreationDTO);
    }

    //Mes tests

    @Test
    void testwrongAuthentification() throws Exception {
        TeamDTO teamDTO = new TeamDTO(null, "AAA");
        mockMvc.perform(post("/admin/team/add").with(csrf())
                        .param("name", teamDTO.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(service, times(1)).addTeam(teamDTO);
    }

}

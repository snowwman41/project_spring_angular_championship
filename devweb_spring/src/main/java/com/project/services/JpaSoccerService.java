package com.project.services;

import com.project.entities.Match;
import jakarta.transaction.Transactional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import com.project.dto.MatchDTO;
import com.project.entities.RankingRow;
import com.project.dto.RankingRowDTO;
import com.project.dto.TeamDTO;
import com.project.repositories.MatchRepository;
import com.project.repositories.RankingRepository;
import com.project.entities.Team;
import com.project.repositories.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import com.project.dto.MatchCreationDTO;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class JpaSoccerService implements SoccerService {
    private final DataSoccerService dataSoccerService;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final RankingRepository rankingRepository;

    public JpaSoccerService(DataSoccerService dataSoccerService,
                            TeamRepository teamRepository,
                            MatchRepository matchRepository,
                            RankingRepository rankingRepository) {
        this.dataSoccerService = dataSoccerService;
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.rankingRepository = rankingRepository;
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        fillDatabase();
    }
    @Transactional
    public void fillDatabase() {
        if (!getRanking().isEmpty()) { return; }
        for (TeamDTO team : dataSoccerService.getTeams()) addTeam(team);
        for (MatchDTO match : dataSoccerService.getMatches()) {
            addMatch(new MatchCreationDTO(
                    match.id(),
                    match.homeTeam().id(), match.awayTeam().id(),
                    match.homeTeamGoals(), match.awayTeamGoals(),
                    match.date(), match.time()));
        }

        /* TODO */
    }
    @Override
    @Transactional
    public void addTeam(TeamDTO team) {
        UUID teamId = team.id() == null ? UUID.randomUUID() : team.id();
        Team entity = new Team(teamId, team.name());
        teamRepository.save(entity);
        addEmptyRankingRow(entity);
        updateRanks();
    }
    @Override
    @Transactional
    public void addMatch(MatchCreationDTO match) {
        UUID id = match.id() == null ? UUID.randomUUID() : match.id();
        Team homeTeam = teamRepository.findById(match.homeTeamId()).orElseThrow();
        Team awayTeam = teamRepository.findById(match.awayTeamId()).orElseThrow();

        int homeTeamGoals = match.homeTeamGoals();
        int awayTeamGoals = match.awayTeamGoals();
        LocalDate date = match.date();
        LocalTime time = match.time();
        Match entity= new Match(id, homeTeam, awayTeam, homeTeamGoals, awayTeamGoals, date, time);
        matchRepository.save(entity);
        updateRankingRow(match.homeTeamId(), match.homeTeamGoals(), match.awayTeamGoals());
        updateRankingRow(match.awayTeamId(), match.awayTeamGoals(), match.homeTeamGoals());
        updateRanks();
        /* TODO */
    }

    @Override
    public List<RankingRowDTO> getRanking() {
        /* TODO */
        return rankingRepository.findAllByOrderByRankAsc().stream().map(JpaSoccerService::toDTO).toList();
    }

    @Override
    public RankingRowDTO getRankingRow(UUID teamId) {
        return rankingRepository.findById(teamId)
                .map(JpaSoccerService::toDTO)
                .orElseThrow();
    }

    @Override
    public List<MatchDTO> getMatches(UUID teamId) {
        return matchRepository.findAllByHomeTeamIdOrAwayTeamIdOrderByDateAscTimeAsc(teamId, teamId).stream()
                .map(JpaSoccerService::toDTO)
                .toList();
        /* TODO */

    }

    @Override
    public List<TeamDTO> getTeams() {
        return teamRepository.findAll().stream()
                .map(JpaSoccerService::toDTO)
                .toList();
    }

    private static TeamDTO toDTO(Team team) {
        return new TeamDTO(team.id, team.name);
    }
    private static MatchDTO toDTO(Match match) {
        return new MatchDTO(match.id, new TeamDTO(match.homeTeam.id, match.homeTeam.name), new TeamDTO(match.awayTeam.id, match.awayTeam.name), match.homeTeamGoals, match.awayTeamGoals,
                match.date, match.time);
    }
    private void addEmptyRankingRow(Team team) {
        RankingRow entity = new RankingRow(team, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        rankingRepository.save(entity);
    }
    private void updateRankingRow(UUID teamId, int goalsForCount, int goalsAgainstCount) {
        RankingRow entity = rankingRepository.findById(teamId).orElseThrow();
        boolean win = goalsForCount > goalsAgainstCount;
        boolean draw = goalsForCount == goalsAgainstCount;
        boolean loss = goalsForCount < goalsAgainstCount;
        entity.matchPlayedCount++;
        entity.matchWonCount += win ? 1 : 0;
        entity.drawCount += draw ? 1 : 0;
        entity.matchLostCount += loss ? 1 : 0;
        entity.goalForCount += goalsForCount;
        entity.goalAgainstCount += goalsAgainstCount;
        entity.goalDifference += goalsForCount - goalsAgainstCount;
        entity.points += win ? 3 : draw ? 1 : 0;
        rankingRepository.save(entity);
    }
    private void updateRanks() {
        int rank = 1;
        for (RankingRow row : rankingRepository.findAllByOrderByPointsDescGoalDifferenceDescGoalForCountDesc()) {
            row.rank = rank;
            rankingRepository.save(row);
            rank++;
        }
    }
    private static RankingRowDTO toDTO(RankingRow rankingRow) {
        return new RankingRowDTO(new TeamDTO(rankingRow.team.id, rankingRow.team.name),
                rankingRow.rank,
                rankingRow.matchPlayedCount,
                rankingRow.matchWonCount,
                rankingRow.matchLostCount,
                rankingRow.drawCount,
                rankingRow.goalForCount,
                rankingRow.goalAgainstCount,
                rankingRow.goalDifference,
                rankingRow.points);
    }
}


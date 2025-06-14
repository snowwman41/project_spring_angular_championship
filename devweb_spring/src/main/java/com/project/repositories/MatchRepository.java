package com.project.repositories;

import com.project.entities.Match;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {
    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    List<Match> findAllByHomeTeamIdOrAwayTeamIdOrderByDateAscTimeAsc(UUID homeTeamId, UUID awayTeamId);
}
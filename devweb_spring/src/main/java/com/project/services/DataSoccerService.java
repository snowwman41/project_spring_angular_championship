package com.project.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.MatchDTO;
import com.project.dto.RankingRowDTO;
import com.project.dto.TeamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.dto.MatchDTO;
import com.project.dto.RankingRowDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
//@Primary
public class DataSoccerService implements SoccerService {
    private final ObjectMapper objectMapper;
    public DataSoccerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private <T> List<T> getList(String filename, Class<T> class_) {
        try {
            File file = ResourceUtils.getFile("classpath:data/"+filename);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, class_);
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
    @Override
    public List<RankingRowDTO> getRanking() {return getList("ranking.json", RankingRowDTO.class);}
    @Override
    public List<TeamDTO> getTeams() {return getList("teams.json", TeamDTO.class);}
    //@Override
    public List<MatchDTO> getMatches() {
        return getList("matches.json", MatchDTO.class);
    }
    @Override
    public RankingRowDTO getRankingRow(UUID teamId) {
        return getRanking()
                .stream()
                .filter(r -> r.team().id().equals(teamId))
                .findFirst()
                .orElseThrow();
    }
    @Override
    public List<MatchDTO> getMatches(UUID teamId) {
        return getMatches()
                .stream()
                .filter(m -> m.homeTeam().id().equals(teamId) || m.awayTeam().id().equals(teamId))
                .toList();
    }

}


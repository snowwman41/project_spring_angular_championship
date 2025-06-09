package com.project.controllers;
import com.project.dto.MatchCreationDTO;
import com.project.dto.MatchDTO;
import com.project.dto.RankingRowDTO;
import com.project.dto.TeamDTO;
import com.project.services.DataSoccerService;
import com.project.services.SoccerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class SoccerController {
    private final SoccerService soccerService;

    public SoccerController(SoccerService soccerService) {
        this.soccerService = soccerService;
    }

    @GetMapping("/")
    public String ranking(Model model) {
        List<RankingRowDTO> ranking = soccerService.getRanking();
        model.addAttribute("ranking", ranking);
        return "ranking";
    }
    @GetMapping("/team/{teamId}")
    public String team(@PathVariable UUID teamId, Model model) {
        List<MatchDTO> matches = soccerService.getMatches(teamId);
        RankingRowDTO row = soccerService.getRankingRow(teamId);

        model.addAttribute("matches", matches);
        model.addAttribute("row", row);
        return "team";
    }
    @GetMapping("/admin/team/add")
    public String showAddTeamForm(TeamDTO teamDTO, Model model) {
        model.addAttribute("teamDTO", teamDTO);
        return "add-team";
    }
    @PostMapping("/admin/team/add")
    public String addTeam(@Valid TeamDTO teamDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showAddTeamForm(teamDTO, model);
        }
        try {
            soccerService.addTeam(teamDTO);
        } catch (Exception e) {
            bindingResult.reject("");
            return showAddTeamForm(teamDTO, model);
        }
        return "redirect:/";
    }
    @GetMapping("/admin/match/add")
    public String showAddMatchForm( MatchCreationDTO matchCreationDTO, Model model) {
        List<TeamDTO> teams =soccerService.getTeams();
        model.addAttribute("matchCreationDTO", matchCreationDTO);
        model.addAttribute("teams", teams);

        return "add-match";
    }

    @PostMapping("/admin/match/add")
    public String addMatch(@Valid MatchCreationDTO matchCreationDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showAddMatchForm(matchCreationDTO, model);
        }
        try {
            soccerService.addMatch(matchCreationDTO);
        } catch (Exception e) {
            bindingResult.reject("");
            return showAddMatchForm(matchCreationDTO, model);
        }
        return "redirect:/";
    }
}
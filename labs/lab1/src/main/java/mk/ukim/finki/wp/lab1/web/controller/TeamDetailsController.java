package mk.ukim.finki.wp.lab1.web.controller;

import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class TeamDetailsController {
    private final TeamService teamService;

    public TeamDetailsController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/teamDetails")
    public String addPlayerToTeam(@RequestParam Long teamId,
                                  @RequestParam(required = false) String playerId) {
        try {
            teamService.addPlayerToTeam(teamId, playerId);
            return "redirect:/teamDetails?teamId=" + teamId;
        } catch (IllegalArgumentException e) {
            return "redirect:/players?teamId=" + teamId + "&errorMessage=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/teamDetails")
    public String getTeamDetailsPage(@RequestParam(required = false) Long teamId, Model model) {
        if (teamId == null) {
            return "redirect:/teams";
        }
        Team team;
        try {
            team = teamService.findById(teamId);
        } catch (IllegalArgumentException e) {
            return "redirect:/teams";
        }
        model.addAttribute("team", team);
        return "teamDetails";
    }
}

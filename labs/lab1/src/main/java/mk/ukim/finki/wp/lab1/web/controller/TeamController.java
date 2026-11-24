package mk.ukim.finki.wp.lab1.web.controller;

import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.service.LeagueService;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class TeamController {
    private final TeamService teamService;
    private final LeagueService leagueService;

    public TeamController(TeamService teamService, LeagueService leagueService) {
        this.teamService = teamService;
        this.leagueService = leagueService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/teams";
    }

    @GetMapping("/teams")
    public String getTeamsPage(@RequestParam(required = false) String errorMessage, Model model) {
        model.addAttribute("teams", teamService.listTeams());
        model.addAttribute("errorMessage", errorMessage);
        return "listTeams";
    }

    @GetMapping("/teams/team-form")
    public String getAddTeamPage(Model model) {
        model.addAttribute("leagues", leagueService.listLeagues());
        return "team-form";
    }

    @GetMapping("/teams/team-form/{id}")
    public String getEditTeamForm(@PathVariable Long id, Model model) {
        Team team;
        try {
            team = teamService.findById(id);
        } catch (IllegalArgumentException e) {
            return "redirect:/teams?errorMessage=TeamNotFound";
        }
        model.addAttribute("team", team);
        model.addAttribute("leagues", leagueService.listLeagues());
        return "team-form";
    }

    @PostMapping("/teams/add")
    public String saveTeam(@RequestParam String name,
                           @RequestParam String city,
                           @RequestParam Long leagueId) {
        try {
            teamService.create(name, city, leagueId);
            return "redirect:/teams";
        } catch (IllegalArgumentException e) {
            return "redirect:/teams?errorMessage=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/teams/edit/{teamId}")
    public String editTeam(@PathVariable Long teamId,
                           @RequestParam String name,
                           @RequestParam String city,
                           @RequestParam Long leagueId) {
        try {
            teamService.update(teamId, name, city, leagueId);
            return "redirect:/teams";
        } catch (IllegalArgumentException e) {
            return "redirect:/teams?errorMessage=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/teams/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
        return "redirect:/teams";
    }
}

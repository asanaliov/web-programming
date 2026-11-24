package mk.ukim.finki.wp.lab1.web.controller;

import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.service.PlayerService;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlayerController {
    private final PlayerService playerService;
    private final TeamService teamService;

    public PlayerController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping("/players")
    public String getPlayersPage(@RequestParam(required = false) Long teamId,
                                 @RequestParam(required = false) String errorMessage,
                                 Model model) {
        if (teamId == null) {
            return "redirect:/teams";
        }
        Team team;
        try {
            team = teamService.findById(teamId);
        } catch (IllegalArgumentException e) {
            return "redirect:/teams";
        }
        model.addAttribute("players", playerService.listPlayers());
        model.addAttribute("team", team);
        model.addAttribute("errorMessage", errorMessage);
        return "playerList";
    }
}

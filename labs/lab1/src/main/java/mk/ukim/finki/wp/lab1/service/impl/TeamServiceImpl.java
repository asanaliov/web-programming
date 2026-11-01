package mk.ukim.finki.wp.lab1.service.impl;

import mk.ukim.finki.wp.lab1.model.Player;
import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.repository.PlayerRepository;
import mk.ukim.finki.wp.lab1.repository.TeamRepository;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamServiceImpl(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Team> listTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Team addPlayerToTeam(Long teamId, String playerId) {
        Team team = findById(teamId);
        Player player = playerRepository.findByPlayerId(playerId).orElseThrow(IllegalArgumentException::new);

        teamRepository.findAll().stream()
                .filter(t -> t.getPlayers().stream()
                        .anyMatch(p -> p.getPlayerId().equals(playerId)))
                .findFirst()
                .ifPresent(t -> {
                    throw new IllegalArgumentException("Player already plays for " + t.getName());
                });
        boolean jerseyTaken = team.getPlayers().stream()
                .anyMatch(p -> p.getJerseyNumber() == player.getJerseyNumber());

        if (jerseyTaken){
            throw new IllegalArgumentException("Jersey number " + player.getJerseyNumber() + " is already taken in this team");
        }

        team.getPlayers().add(player);
        return teamRepository.save(team);
    }
}

package mk.ukim.finki.wp.lab1.service.impl;

import mk.ukim.finki.wp.lab1.model.League;
import mk.ukim.finki.wp.lab1.model.Player;
import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.repository.LeagueRepository;
import mk.ukim.finki.wp.lab1.repository.PlayerRepository;
import mk.ukim.finki.wp.lab1.repository.TeamRepository;
import mk.ukim.finki.wp.lab1.service.TeamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;

    public TeamServiceImpl(TeamRepository teamRepository, PlayerRepository playerRepository, LeagueRepository leagueRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.leagueRepository = leagueRepository;
    }

    @Override
    public List<Team> listTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }

    @Override
    public Team addPlayerToTeam(Long teamId, String playerId) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Please select a player");
        }
        Team team = findById(teamId);
        Player player = playerRepository.findByPlayerId(playerId).orElseThrow(() -> new IllegalArgumentException("Player not found"));

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

    @Override
    public Team create(String name, String city, Long leagueId) {
        League league = validate(name, city, leagueId);
        return teamRepository.save(new Team(name, city, new ArrayList<>(), league));
    }

    @Override
    public Team update(Long id, String name, String city, Long leagueId) {
        League league = validate(name, city, leagueId);
        Team team = findById(id);
        team.setName(name);
        team.setCity(city);
        team.setLeague(league);
        return teamRepository.save(team);
    }

    @Override
    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

    private League validate(String name, String city, Long leagueId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City must not be empty");
        }
        return leagueRepository.findById(leagueId).orElseThrow(() -> new IllegalArgumentException("League not found"));
    }
}

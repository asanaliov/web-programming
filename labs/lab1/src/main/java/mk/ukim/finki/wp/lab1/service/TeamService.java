package mk.ukim.finki.wp.lab1.service;

import mk.ukim.finki.wp.lab1.model.Team;

import java.util.List;

public interface TeamService {
    List<Team> listTeams();
    Team findById(Long id);
    Team addPlayerToTeam(Long teamId, String playerId);
    Team create(String name, String city, Long leagueId);
    Team update(Long id, String name, String city, Long leagueId);
    void deleteById(Long id);
}

package mk.ukim.finki.wp.lab1.repository.impl;

import mk.ukim.finki.wp.lab1.bootstrap.DataHolder;
import mk.ukim.finki.wp.lab1.model.Team;
import mk.ukim.finki.wp.lab1.repository.TeamRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryTeamRepository implements TeamRepository {

    @Override
    public List<Team> findAll() {
        return DataHolder.teams;
    }

    @Override
    public Optional<Team> findById(Long id) {
        return DataHolder.teams.stream().filter(team -> team.getId().equals(id)).findFirst();
    }

    @Override
    public Team save(Team team) {
        Optional<Team> existing = DataHolder.teams.stream()
                .filter(team1 -> team1.getId().equals(team.getId()))
                .findFirst();
        if (existing.isPresent()) {
            int idx = DataHolder.teams.indexOf(existing.get());
            DataHolder.teams.set(idx, team);
        } else {
            DataHolder.teams.add(team);
        }
        return team;
    }
}
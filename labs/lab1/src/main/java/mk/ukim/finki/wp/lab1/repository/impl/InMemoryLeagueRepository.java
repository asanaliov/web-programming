package mk.ukim.finki.wp.lab1.repository.impl;

import mk.ukim.finki.wp.lab1.bootstrap.DataHolder;
import mk.ukim.finki.wp.lab1.model.League;
import mk.ukim.finki.wp.lab1.repository.LeagueRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryLeagueRepository implements LeagueRepository {
    @Override
    public List<League> findAll() {
        return DataHolder.leagues;
    }

    @Override
    public Optional<League> findById(Long id) {
        return DataHolder.leagues.stream().filter(league -> league.getId().equals(id)).findFirst();
    }
}

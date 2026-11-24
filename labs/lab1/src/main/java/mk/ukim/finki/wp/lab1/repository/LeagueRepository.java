package mk.ukim.finki.wp.lab1.repository;

import mk.ukim.finki.wp.lab1.model.League;

import java.util.List;
import java.util.Optional;

public interface LeagueRepository {
    List<League> findAll();
    Optional<League> findById(Long id);
}

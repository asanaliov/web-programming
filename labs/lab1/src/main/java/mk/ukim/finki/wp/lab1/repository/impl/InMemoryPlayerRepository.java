package mk.ukim.finki.wp.lab1.repository.impl;

import mk.ukim.finki.wp.lab1.bootstrap.DataHolder;
import mk.ukim.finki.wp.lab1.model.Player;
import mk.ukim.finki.wp.lab1.repository.PlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class InMemoryPlayerRepository implements PlayerRepository {
    @Override
    public List<Player> findAll() {
        return DataHolder.players;
    }

    @Override
    public Optional<Player> findByPlayerId(String playerId) {
        return DataHolder.players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst();
    }
}

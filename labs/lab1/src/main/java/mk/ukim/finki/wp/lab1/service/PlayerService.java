package mk.ukim.finki.wp.lab1.service;

import mk.ukim.finki.wp.lab1.model.Player;

import java.util.List;

public interface PlayerService {
    List<Player> listPlayers();
    Player findByPlayerId(String playerId);
}


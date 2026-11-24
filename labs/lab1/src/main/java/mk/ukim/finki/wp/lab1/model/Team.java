package mk.ukim.finki.wp.lab1.model;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Team {
    private Long id;
    private String name;
    private String city;
    private List<Player> players;
    private League league;

    public Team(String name, String city, List<Player> players, League league) {
        this.id = (long) (Math.random() * 1000);
        this.name = name;
        this.city = city;
        this.players = players;
        this.league = league;
    }
}

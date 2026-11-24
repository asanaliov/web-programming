package mk.ukim.finki.wp.lab1.bootstrap;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.lab1.model.League;
import mk.ukim.finki.wp.lab1.model.Player;
import mk.ukim.finki.wp.lab1.model.Team;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataHolder {
    public static List<Team> teams = null;
    public static List<Player> players = null;
    public static List<League> leagues = null;

    @PostConstruct
    public void init(){
        leagues = new ArrayList<>();
        leagues.add(new League(1L, "NBA", "USA", "National Basketball Association"));
        leagues.add(new League(2L, "EuroLeague", "Europe", "Top-tier European club competition"));
        leagues.add(new League(3L, "ABA League", "Balkans", "Adriatic Basketball Association"));

        teams = new ArrayList<>();
        teams.add(new Team("Chicago Bulls", "Chicago", new ArrayList<>(), leagues.get(0)));
        teams.add(new Team("Los Angeles Lakers", "Los Angeles", new ArrayList<>(), leagues.get(0)));
        teams.add(new Team("Golden State Warriors", "San Francisco", new ArrayList<>(), leagues.get(0)));
        teams.add(new Team("Boston Celtics", "Boston", new ArrayList<>(), leagues.get(0)));
        teams.add(new Team("Dallas Mavericks", "Dallas", new ArrayList<>(), leagues.get(0)));

        players = new ArrayList<>();
        players.add(new Player("P1", "Michael", "Jordan", "Shooting Guard", 23));
        players.add(new Player("P2", "LeBron", "James", "Small Forward", 23));
        players.add(new Player("P3", "Stephen", "Curry", "Point Guard", 30));
        players.add(new Player("P4", "Kevin", "Durant", "Small Forward", 7));
        players.add(new Player("P5", "Luka", "Doncic", "Point Guard", 77));
        players.add(new Player("P6", "Nikola", "Jokic", "Center", 15));
        players.add(new Player("P7", "Giannis", "Antetokounmpo", "Power Forward", 34));
        players.add(new Player("P8", "Kobe", "Bryant", "Shooting Guard", 24));
        players.add(new Player("P9", "Kyrie", "Irving", "Point Guard", 11));
        players.add(new Player("P10", "Kevin", "Garnett", "Power Forward", 5));
    }
}

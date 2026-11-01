package mk.ukim.finki.wp.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private String playerId;
    private String firstName;
    private String lastName;
    private String position;
    private int jerseyNumber;
}

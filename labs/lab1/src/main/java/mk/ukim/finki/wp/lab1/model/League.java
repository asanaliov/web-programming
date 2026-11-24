package mk.ukim.finki.wp.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class League {
    private Long id;
    private String name;
    private String country;
    private String description;
}

package by.kovalevskiy.FootballBaseGradle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @ManyToMany
    @JoinTable(name = "player_2_game",
            joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "game_id", referencedColumnName = "id"))
    private Set<Game> games = new HashSet<Game>();

    @OneToMany(mappedBy = "player")
    private List<Comment> comments;
    @Id
    @Column(name = "id")
    private int id;
    @NotEmpty(message = "name should not be empty")
    @Size(min = 2, max = 20, message = "allowed from 2 to 20 characters")
    @Column(name = "name")
    private String name;
    @NotEmpty(message = "surname should not be empty")
    @Size(min = 2, max = 30, message = "allowed from 2 to 30 characters")
    @Column(name = "surname")
    private String surname;
    @Min(value = 4, message = "player should be older than 3 years")
    @Column(name = "age")
    private int age;
    @NotEmpty(message = "city should not be empty")
    @Column(name = "city")
    private String city;
    @Column(name = "created")
    private LocalDate created;
    @Column(name = "updated")
    private LocalDate updated;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;
    @Column(name = "enabled")
    private Boolean enabled;


    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

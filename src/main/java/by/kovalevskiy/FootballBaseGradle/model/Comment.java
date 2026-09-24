package by.kovalevskiy.FootballBaseGradle.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;
    @ManyToOne
    @JoinColumn(name = "game_id",referencedColumnName = "id")
    private Game game;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "text")
    @NotEmpty(message = "you should write something")
    @Size(min = 1,max = 500,message = "allowed from 1 to 500 characters")
    private String text;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
